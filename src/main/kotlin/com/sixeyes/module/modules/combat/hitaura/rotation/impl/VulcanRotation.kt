package com.sixeyes.module.modules.combat.hitaura.rotation.impl

import com.sixeyes.client.util.rotation.Rotation
import com.sixeyes.module.modules.combat.hitaura.rotation.RotationStrategy
import net.minecraft.world.entity.LivingEntity
import java.util.concurrent.ThreadLocalRandom

class VulcanRotation : RotationStrategy("Vulcan") {
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
        val maxYawStep = (speed.coerceAtMost(70.0f) + rng.nextFloat(-3.5f, 3.5f)).coerceAtLeast(5.0f)
        val maxPitchStep = ((speed * 0.6f).coerceAtMost(42.0f) + rng.nextFloat(-2.0f, 2.0f)).coerceAtLeast(3.0f)

        val stepYaw = deltaYaw.coerceIn(-maxYawStep, maxYawStep)
        val stepPitch = (deltaPitch * 0.72f).coerceIn(-maxPitchStep, maxPitchStep)

        return Rotation(
            current.yaw + stepYaw,
            (current.pitch + stepPitch).coerceIn(-90.0f, 90.0f)
        )
    }
}
