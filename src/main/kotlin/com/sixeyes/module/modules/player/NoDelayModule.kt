package com.sixeyes.module.modules.player

import com.sixeyes.client.extensions.PLAYER
import com.sixeyes.client.extensions.mc
import com.sixeyes.event.events.PlayerUpdateEvent
import com.sixeyes.module.Module
import sweetie.evaware.flora.api.Commando

object NoDelayModule : Module(
    name = "NoDelay",
    category = PLAYER,
    description = ""
) {
    val jump by boolean("Jump", true, "")
    val place by boolean("Place", true, "")

    @Commando
    fun onUpdate(event: PlayerUpdateEvent) {
        safeRun {
            if (jump) {
                player.noJumpDelay = 0
            }

            if (place) {
                mc.rightClickDelay = 0
            }
        }
    }
}
