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
        val fontSize = mc.window.guiScaledHeight * 0.02f
        val rectX = 8f
        val rectY = 8f
        val rectWidth = 80f
        val rectHeight = 26f

        RenderUtils.roundedRect(
            matrix = MatrixControl.matrix4fStack,
            x = rectX,
            y = rectY,
            width = rectWidth,
            height = rectHeight,
            color = Color4b(16, 16, 16, 170),
            radius = 8f,
            pipeline = ClientRenderPipeline.FIRST_RECT
        )

        font.drawString(
            matrix = MatrixControl.matrix4fStack,
            text = text,
            x = rectX + 11f,
            y = rectY + 7f,
            size = fontSize,
            color = Color4b.WHITE,
            pipeline = ClientRenderPipeline.FIRST_TEXT
        )
    }
}