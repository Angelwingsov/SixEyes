package com.sixeyes.module.modules

import com.sixeyes.client.extensions.OTHER
import com.sixeyes.event.events.PlayerUpdateEvent
import com.sixeyes.module.Module
import sweetie.evaware.flora.api.Commando

object TestModule : Module(
    name = "Test",
    category = OTHER
) {
    init {
        setEnabled(true)
    }

    @Commando
    fun onPlayerUpdate(event: PlayerUpdateEvent) {
        println("test")
    }
}