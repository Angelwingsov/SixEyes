package com.sixeyes.module.modules.combat.hitaura.rotation.impl

import com.sixeyes.client.util.rotation.Rotation
import com.sixeyes.module.modules.combat.hitaura.rotation.RotationStrategy
import net.minecraft.world.entity.LivingEntity
import java.util.concurrent.ThreadLocalRandom

class SmoothRotation : RotationStrategy("Smooth") {
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
        val smoothFactor = (speed / 180.0f).coerceIn(0.08f, 1.0f)
        val dynamicEase = (smoothFactor * (0.65f + 0.35f * (totalAngle / 90.0f).coerceIn(0.0f, 1.0f)))
            .coerceIn(0.05f, 1.0f)

        val jitterYaw = rng.nextGaussian().toFloat() * 0.18f
        val jitterPitch = rng.nextGaussian().toFloat() * 0.14f

        val maxStep = speed * 1.15f
        val stepYaw = (deltaYaw * dynamicEase + jitterYaw).coerceIn(-maxStep, maxStep)
        val stepPitch = (deltaPitch * (dynamicEase * 0.85f) + jitterPitch).coerceIn(-maxStep, maxStep)

        return Rotation(
            current.yaw + stepYaw,
            (current.pitch + stepPitch).coerceIn(-90.0f, 90.0f)
        )
    }
}
