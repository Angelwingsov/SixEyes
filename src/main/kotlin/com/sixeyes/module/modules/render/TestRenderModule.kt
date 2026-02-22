package com.sixeyes.module.modules.render

import com.sixeyes.client.extensions.RENDER
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.engine.controls.MatrixControl
import com.sixeyes.client.util.render.font.Fonts
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
        val font = Fonts.google_sans_regular
        val text = "Hello 6 Eyes"
        val fontSize = mc.window.guiScaledHeight * 0.03f
        font.drawString(
            matrix = MatrixControl.matrix4fStack,
            text = text,
            x = 3f,
            y = 3f,
            size = fontSize,
            color = Color4b.WHITE,
            pipeline = ClientRenderPipeline.FIRST_TEXT
        )
    }
}