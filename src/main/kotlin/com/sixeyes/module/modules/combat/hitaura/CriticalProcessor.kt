package com.sixeyes.module.modules.combat.hitaura

import com.sixeyes.client.extensions.inGame
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.extensions.player
import com.sixeyes.client.util.movement.MoveUtil
import net.minecraft.world.effect.MobEffects

class CriticalProcessor {
    fun isReadyForAttack(onlyCrits: Boolean, smartCrits: Boolean): Boolean {
        if (!inGame) return false
        if (!onlyCrits) return true

        if (!canPerformCrit()) {
            return true
        }

        if (smartCrits) {
            val isJumpingOrAirborne = !player.onGround() || mc.options.keyJump.isDown
            if (!isJumpingOrAirborne) {
                return true
            }
        }

        return !player.onGround() && player.deltaMovement.y < 0.0
    }

    private fun canPerformCrit(): Boolean {
        if (player.isInWater || player.isInLava || player.onClimbable() || player.isPassenger) {
            return false
        }
        if (player.abilities.flying || player.isFallFlying) {
            return false
        }
        if (MoveUtil.isInWeb() || MoveUtil.isAboveWater()) {
            return false
        }
        if (player.hasEffect(MobEffects.BLINDNESS)
            || player.hasEffect(MobEffects.LEVITATION)
            || player.hasEffect(MobEffects.SLOW_FALLING)
        ) {
            return false
        }
        return true
    }
}
