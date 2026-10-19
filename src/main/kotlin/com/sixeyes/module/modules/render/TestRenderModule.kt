package com.sixeyes.module.modules.render

import com.sixeyes.client.extensions.RENDER
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.ScissorUtil
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
        val matrix = MatrixControl.matrix4fStack
        val x = 12f
        val y = 12f
        val w = 310f
        val h = 240f

        RenderUtils.roundedRect(
            matrix = matrix,
            x = x,
            y = y,
            width = w,
            height = h,
            color = Color4b(18, 18, 22, 220),
            radius = 10f,
            pipeline = ClientRenderPipeline.FIRST_RECT
        )

        RenderUtils.roundedRect(
            matrix = matrix,
            x = x,
            y = y,
            width = w,
            height = 28f,
            color = Color4b(28, 28, 36, 180),
            radius = 10f,
            pipeline = ClientRenderPipeline.FIRST_RECT
        )

        Fonts.google_sans_bold.drawString(
            matrix = matrix,
            text = "SixEyes Render Showcase",
            x = x + 10f,
            y = y + 7f,
            size = 14f,
            color = Color4b.WHITE,
            shadow = true,
            pipeline = ClientRenderPipeline.FIRST_TEXT
        )

        val badgeText = "ACTIVE"
        val badgeW = Fonts.google_sans_bold.width(badgeText, 10f) + 12f
        val badgeX = x + w - badgeW - 8f
        val badgeY = y + 6f
        RenderUtils.roundedRect(
            matrix = matrix,
            x = badgeX,
            y = badgeY,
            width = badgeW,
            height = 16f,
            color = Color4b(40, 180, 100, 60),
            radius = 4f,
            pipeline = ClientRenderPipeline.FIRST_RECT
        )
        Fonts.google_sans_bold.drawString(
            matrix = matrix,
            text = badgeText,
            x = badgeX + 6f,
            y = badgeY + 3f,
            size = 10f,
            color = Color4b(80, 240, 140, 255),
            shadow = false,
            pipeline = ClientRenderPipeline.FIRST_TEXT
        )

        var curY = y + 36f

        Fonts.google_sans_regular.drawString(
            matrix = matrix,
            text = "Google Sans Regular: 12.0px Text Demo",
            x = x + 10f,
            y = curY,
            size = 12f,
            color = Color4b(230, 230, 235, 255),
            shadow = true,
            pipeline = ClientRenderPipeline.FIRST_TEXT
        )
        curY += 16f

        Fonts.google_sans_medium.drawString(
            matrix = matrix,
            text = "Google Sans Medium: 12.0px Typography",
            x = x + 10f,
            y = curY,
            size = 12f,
            color = Color4b(200, 210, 255, 255),
            shadow = true,
            pipeline = ClientRenderPipeline.FIRST_TEXT
        )
        curY += 16f

        Fonts.product_sans_regular.drawString(
            matrix = matrix,
            text = "Product Sans Regular: Modern Client UI",
            x = x + 10f,
            y = curY,
            size = 12f,
            color = Color4b(255, 220, 180, 255),
            shadow = true,
            pipeline = ClientRenderPipeline.FIRST_TEXT
        )
        curY += 20f

        Fonts.google_sans_regular.drawString(
            matrix = matrix,
            text = "§cRed §6Gold §eYellow §aGreen §bAqua §dPink §fWhite §r(Reset)",
            x = x + 10f,
            y = curY,
            size = 11.5f,
            color = Color4b.WHITE,
            shadow = true,
            pipeline = ClientRenderPipeline.FIRST_TEXT
        )
        curY += 18f

        Fonts.google_sans_medium.drawString(
            matrix = matrix,
            text = "Left Aligned",
            x = x + 10f,
            y = curY,
            size = 11f,
            color = Color4b(180, 180, 190, 255),
            shadow = true,
            pipeline = ClientRenderPipeline.FIRST_TEXT
        )
        Fonts.google_sans_medium.drawCenteredString(
            matrix = matrix,
            text = "Centered",
            centerX = x + w * 0.5f,
            y = curY,
            size = 11f,
            color = Color4b(180, 180, 190, 255),
            shadow = true,
            pipeline = ClientRenderPipeline.FIRST_TEXT
        )
        Fonts.google_sans_medium.drawRightString(
            matrix = matrix,
            text = "Right Aligned",
            rightX = x + w - 10f,
            y = curY,
            size = 11f,
            color = Color4b(180, 180, 190, 255),
            shadow = true,
            pipeline = ClientRenderPipeline.FIRST_TEXT
        )
        curY += 20f

        RenderUtils.gradientRect(
            matrix = matrix,
            x = x + 10f,
            y = curY,
            width = w - 20f,
            height = 8f,
            topLeft = Color4b(130, 80, 255, 255),
            topRight = Color4b(0, 220, 255, 255),
            bottomRight = Color4b(0, 220, 255, 255),
            bottomLeft = Color4b(130, 80, 255, 255),
            pipeline = ClientRenderPipeline.FIRST_RECT
        )
        curY += 16f

        Fonts.google_sans_regular.drawString(
            matrix = matrix,
            text = "Shadow: Off",
            x = x + 10f,
            y = curY,
            size = 11f,
            color = Color4b(220, 220, 220, 255),
            shadow = false,
            pipeline = ClientRenderPipeline.FIRST_TEXT
        )
        Fonts.google_sans_regular.drawString(
            matrix = matrix,
            text = "Shadow: On (Drop Shadow)",
            x = x + 120f,
            y = curY,
            size = 11f,
            color = Color4b(220, 220, 220, 255),
            shadow = true,
            pipeline = ClientRenderPipeline.FIRST_TEXT
        )
        curY += 20f

        val clipX = x + 10f
        val clipY = curY
        val clipW = w - 20f
        val clipH = 22f

        RenderUtils.roundedRect(
            matrix = matrix,
            x = clipX,
            y = clipY,
            width = clipW,
            height = clipH,
            color = Color4b(10, 10, 14, 160),
            radius = 4f,
            pipeline = ClientRenderPipeline.FIRST_RECT
        )

        ScissorUtil.start(clipX, clipY, clipW, clipH)
        Fonts.google_sans_regular.drawString(
            matrix = matrix,
            text = "Scissor Test: [This long text will be clipped strictly inside the bounding box and will not overflow]",
            x = clipX + 6f,
            y = clipY + 5f,
            size = 11f,
            color = Color4b(120, 200, 255, 255),
            shadow = true,
            pipeline = ClientRenderPipeline.FIRST_TEXT
        )
        ScissorUtil.end()
    }
}