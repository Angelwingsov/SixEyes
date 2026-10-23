package com.sixeyes.module.modules.combat.hitaura.rotation.impl

import com.sixeyes.client.util.rotation.Rotation
import com.sixeyes.module.modules.combat.hitaura.rotation.RotationStrategy
import net.minecraft.world.entity.LivingEntity
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.sin

class GrimRotation : RotationStrategy("Grim") {
    override fun calculate(
        current: Rotation,
        ideal: Rotation,
        deltaYaw: Float,
        deltaPitch: Float,
        totalAngle: Float,
        target: LivingEntity,
        speed: Float,
        tickCounter: Int
    ): Rotation {
        val rng = ThreadLocalRandom.current()
        val maxYawStep = (speed * 1.1f).coerceAtMost(115.0f)
        val maxPitchStep = (speed * 0.85f).coerceAtMost(75.0f)
        val accel = (0.55f + 0.45f * sin((tickCounter % 8) * 0.785f)).coerceIn(0.45f, 1.0f)

        val microYaw = rng.nextFloat(-0.25f, 0.25f)
        val microPitch = rng.nextFloat(-0.20f, 0.20f)

        val stepYaw = (deltaYaw * 0.78f * accel + microYaw).coerceIn(-maxYawStep, maxYawStep)
        val stepPitch = (deltaPitch * 0.68f * accel + microPitch).coerceIn(-maxPitchStep, maxPitchStep)

        return Rotation(
            current.yaw + stepYaw,
            (current.pitch + stepPitch).coerceIn(-90.0f, 90.0f)
        )
    }
}
