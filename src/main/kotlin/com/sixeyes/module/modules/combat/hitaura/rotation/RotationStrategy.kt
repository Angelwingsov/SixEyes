package com.sixeyes.module.modules.combat.hitaura.rotation

import com.sixeyes.client.util.rotation.Rotation
import net.minecraft.world.entity.LivingEntity

abstract class RotationStrategy(
    val name: String,
    open val keepTicks: Int = 1
) {
    open fun reset() {}

    open fun shouldUpdateRotation(shouldSnapNow: Boolean): Boolean = true

    abstract fun calculate(
        current: Rotation,
        ideal: Rotation,
        deltaYaw: Float,
        deltaPitch: Float,
        totalAngle: Float,
        target: LivingEntity,
        speed: Float,
        tickCounter: Int
    ): Rotation
}
