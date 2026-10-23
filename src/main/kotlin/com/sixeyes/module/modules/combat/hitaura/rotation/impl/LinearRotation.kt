package com.sixeyes.module.modules.combat.hitaura.rotation.impl

import com.sixeyes.client.util.rotation.Rotation
import com.sixeyes.module.modules.combat.hitaura.rotation.RotationStrategy
import net.minecraft.world.entity.LivingEntity

class LinearRotation : RotationStrategy("Linear") {
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
        val factor = (speed / totalAngle).coerceIn(0.0f, 1.0f)
        return Rotation(
            current.yaw + deltaYaw * factor,
            (current.pitch + deltaPitch * factor).coerceIn(-90.0f, 90.0f)
        )
    }
}
