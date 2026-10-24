package com.sixeyes.module.modules.combat.hitaura

import com.sixeyes.client.extensions.box
import com.sixeyes.client.extensions.player
import com.sixeyes.client.util.rotation.*
import com.sixeyes.module.modules.combat.HitAuraModule.AimPointMode
import com.sixeyes.module.modules.combat.HitAuraModule.RotationMode
import com.sixeyes.module.modules.combat.hitaura.rotation.RotationStrategy
import com.sixeyes.module.modules.combat.hitaura.rotation.impl.*
import net.minecraft.util.Mth
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.Vec3
import kotlin.math.abs
import kotlin.math.hypot

class AuraRotationProcessor {
    private var tickCounter: Int = 0

    private val strategies: Map<RotationMode, RotationStrategy> = mapOf(
        RotationMode.SMOOTH to SmoothRotation(),
        RotationMode.LINEAR to LinearRotation(),
        RotationMode.GRIM to GrimRotation(),
        RotationMode.VULCAN to VulcanRotation(),
        RotationMode.MATRIX to MatrixRotation(),
        RotationMode.SNAP to SnapRotation()
    )

    fun getStrategy(mode: RotationMode): RotationStrategy {
        return strategies[mode] ?: strategies.getValue(RotationMode.SMOOTH)
    }

    fun reset() {
        tickCounter = 0
        strategies.values.forEach(RotationStrategy::reset)
    }

    fun processRotation(
        target: LivingEntity,
        rotationMode: RotationMode,
        aimPointMode: AimPointMode,
        rotationSpeed: Float,
        gcdEnabled: Boolean,
        moveFixMode: MoveFixMode,
        shouldSnapNow: Boolean
    ): Rotation {
        tickCounter++

        val strategy = getStrategy(rotationMode)
        val currentRot = RotationManager.getServerRotation()

        if (!strategy.shouldUpdateRotation(shouldSnapNow)) {
            return currentRot
        }

        val aimPoint = calculateAimPoint(target, aimPointMode)
        val idealRotation = RotationUtil.getRotationToVec(aimPoint).normalizeTo(currentRot)

        val dYaw = Mth.wrapDegrees(idealRotation.yaw - currentRot.yaw)
        val dPitch = idealRotation.pitch - currentRot.pitch
        val totalAngle = hypot(dYaw, dPitch)

        if (totalAngle <= 0.01f) {
            RotationManager.setRotation(
                rotation = currentRot,
                moveFix = moveFixMode,
                gcd = gcdEnabled,
                keepTicks = strategy.keepTicks
            )
            return RotationManager.getServerRotation()
        }

        val steppedRotation = strategy.calculate(
            current = currentRot,
            ideal = idealRotation,
            deltaYaw = dYaw,
            deltaPitch = dPitch,
            totalAngle = totalAngle,
            target = target,
            speed = rotationSpeed,
            tickCounter = tickCounter
        )

        RotationManager.setRotation(
            rotation = steppedRotation,
            moveFix = moveFixMode,
            gcd = gcdEnabled,
            keepTicks = strategy.keepTicks,
            returnSpeed = rotationSpeed.coerceAtLeast(40.0f)
        )

        return RotationManager.getServerRotation()
    }

    fun isAimedAtTarget(target: LivingEntity, maxAngleError: Float = 28.0f): Boolean {
        val currentRot = RotationManager.getServerRotation()
        val nearestVec = RotationUtil.getNearestPoint(player.eyePosition, target.box)
        val neededRot = RotationUtil.getRotationToVec(nearestVec)
        val yawError = abs(Mth.wrapDegrees(neededRot.yaw - currentRot.yaw))
        val pitchError = abs(neededRot.pitch - currentRot.pitch)
        return yawError <= maxAngleError && pitchError <= (maxAngleError * 1.35f)
    }

    private fun calculateAimPoint(target: LivingEntity, aimPointMode: AimPointMode): Vec3 {
        val box = target.box
        val eyePos = player.eyePosition
        return when (aimPointMode) {
            AimPointMode.NEAREST -> RotationUtil.getNearestPoint(eyePos, box, 0.10)
            AimPointMode.MULTIPOINT -> RotationUtil.getMultipoint(eyePos, box, 0.78)
            AimPointMode.HEAD -> Vec3(
                box.center.x,
                (box.minY + (box.maxY - box.minY) * 0.88).coerceAtMost(box.maxY - 0.05),
                box.center.z
            )
            AimPointMode.CHEST -> Vec3(
                box.center.x,
                box.minY + (box.maxY - box.minY) * 0.55,
                box.center.z
            )
        }
    }
}
