package com.sixeyes.module.modules.player

import com.sixeyes.client.extensions.PLAYER
import com.sixeyes.client.extensions.inGame
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.extensions.player
import com.sixeyes.event.events.PlayerUpdateEvent
import com.sixeyes.module.Module
import sweetie.evaware.flora.api.Commando

object NoDelayModule : Module(
    name = "NoDelay",
    category = PLAYER,
    description = ""
) {
    val jump = boolean("Jump", true, "")
    val place = boolean("Place", true, "")

    @Commando
    fun onUpdate(event: PlayerUpdateEvent) {
        if (!inGame) return

        if (jump.value) {
            player.noJumpDelay = 0
        }

        if (place.value) {
            mc.rightClickDelay = 0
        }
    }
}
