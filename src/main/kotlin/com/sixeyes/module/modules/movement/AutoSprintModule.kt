package com.sixeyes.module.modules.movement

import com.sixeyes.client.extensions.MOVEMENT
import com.sixeyes.client.extensions.currentScreen
import com.sixeyes.client.extensions.inGame
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.util.movement.MoveUtil
import com.sixeyes.event.events.PlayerUpdateEvent
import com.sixeyes.module.Module
import com.sixeyes.module.modules.combat.HitAuraModule
import sweetie.evaware.flora.api.Commando

object AutoSprintModule : Module(
    name = "AutoSprint",
    category = MOVEMENT,
    description = ""
) {
    fun shouldSprint(): Boolean {
        if (!isEnabled() || !inGame) return false
        if (HitAuraModule.shouldPreventSprint()) return false
        if (InventoryMoveModule.shouldPreventSprint()) return false
        val screen = currentScreen
        if (screen != null && !InventoryMoveModule.canMoveInScreen(screen)) return false
        return true
    }

    override fun onDisable() {
        if (inGame) {
            mc.options.keySprint.isDown = MoveUtil.isKeyPressed(mc.options.keySprint)
        }
    }

    @Commando
    fun onUpdate(event: PlayerUpdateEvent) {
        safeRun {
            if (HitAuraModule.shouldPreventSprint() || InventoryMoveModule.shouldPreventSprint()) {
                player.isSprinting = false
                return@safeRun
            }

            if (shouldSprint()) {
                mc.options.keySprint.isDown = true
            } else {
                mc.options.keySprint.isDown = MoveUtil.isKeyPressed(mc.options.keySprint)
            }
        }
    }
}
