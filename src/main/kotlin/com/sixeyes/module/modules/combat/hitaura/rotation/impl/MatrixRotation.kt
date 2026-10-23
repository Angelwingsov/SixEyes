package com.sixeyes.module.modules.combat.hitaura.rotation.impl

import com.sixeyes.client.util.rotation.Rotation
import com.sixeyes.module.modules.combat.hitaura.rotation.RotationStrategy
import net.minecraft.world.entity.LivingEntity
import kotlin.math.cos

class MatrixRotation : RotationStrategy("Matrix") {
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
        val wave = cos(tickCounter * 0.65).toFloat() * 0.45f
        val maxYawStep = (speed * 0.95f).coerceAtMost(85.0f)
        val maxPitchStep = (speed * 0.55f).coerceAtMost(50.0f)

        val stepYaw = (deltaYaw * 0.74f + wave).coerceIn(-maxYawStep, maxYawStep)
        val stepPitch = (deltaPitch * 0.52f - wave * 0.5f).coerceIn(-maxPitchStep, maxPitchStep)

        return Rotation(
            current.yaw + stepYaw,
            (current.pitch + stepPitch).coerceIn(-90.0f, 90.0f)
        )
    }
}
