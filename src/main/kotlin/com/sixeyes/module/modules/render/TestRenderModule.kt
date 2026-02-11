package com.sixeyes.module.modules.render

import com.sixeyes.client.extensions.RENDER
import com.sixeyes.event.events.OverlayRenderEvent
import com.sixeyes.module.Module
import sweetie.evaware.flora.api.Commando

object TestRenderModule : Module(
    name = "Test Render",
    category = RENDER
) {
    init {
        setEnabled(true)
    }

    @Commando
    fun onOverlayRender(event: OverlayRenderEvent) {
        val partialTicks = event.get(OverlayRenderEvent.PARTIAL_TICKS) ?: return
    }
}