package com.sixeyes.module.modules.combat.hitaura

import com.sixeyes.client.extensions.inGame
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.extensions.player
import com.sixeyes.client.util.movement.MoveUtil
import com.sixeyes.client.util.rotation.MoveFixProcessor
import com.sixeyes.module.modules.combat.HitAuraModule.SprintResetMode
import com.sixeyes.module.modules.movement.AutoSprintModule

class SprintResetProcessor {
    private var legitResetTicks: Int = 0

    fun isResettingSprint(): Boolean = legitResetTicks > 0

    fun reset() {
        legitResetTicks = 0
    }

    fun onTick() {
        if (!inGame) return
        if (legitResetTicks > 0) {
            legitResetTicks--
            if (legitResetTicks == 0) {
                val shouldSprint = mc.options.keySprint.isDown
                    || MoveUtil.isKeyPressed(mc.options.keySprint)
                    || AutoSprintModule.isEnabled()
                    || MoveFixProcessor.hasForwardInput
                if (shouldSprint) {
                    player.isSprinting = true
                    mc.options.keySprint.isDown = true
                }
            } else {
                player.isSprinting = false
            }
        }
    }

    fun preAttack(mode: SprintResetMode, forceStopSprint: Boolean = false) {
        if (!inGame) return
        if (forceStopSprint) {
            player.isSprinting = false
        }
    }

    fun postAttack(mode: SprintResetMode) {
        if (!inGame || mode == SprintResetMode.OFF) return

        player.isSprinting = false
        legitResetTicks = 1
    }
}
