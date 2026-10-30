package com.sixeyes.client.util.render

import com.sixeyes.client.render.texture.texture.GLTexture
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.rect.RectBorder
import com.sixeyes.client.util.render.rect.RectColors
import com.sixeyes.client.util.render.rect.RectCorners
import com.sixeyes.client.util.render.rect.RectRenderer
import com.sixeyes.client.util.render.rect.RectStyle
import com.sixeyes.client.util.render.rect.RectType
import com.sixeyes.client.util.render.rect.RectUv
import net.minecraft.client.gui.GuiGraphicsExtractor
import org.joml.Matrix4f

object RenderUtils {
    var currentGraphics: GuiGraphicsExtractor? = null

    fun rect(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Color4b,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        val gfx = currentGraphics
        if (gfx != null) {
            VulkanShapes.drawRect(gfx, x, y, width, height, color)
            return
        }

        val r = color.r / 255f
        val g = color.g / 255f
        val b = color.b / 255f
        val a = color.a / 255f

        RectRenderer.emitDirect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            trR = r, trG = g, trB = b, trA = a,
            tlR = r, tlG = g, tlB = b, tlA = a,
            brR = r, brG = g, brB = b, brA = a,
            blR = r, blG = g, blB = b, blA = a,
            radiusTl = 0f, radiusTr = 0f, radiusBr = 0f, radiusBl = 0f,
            pipeline = pipeline
        )
    }

    fun roundedRect(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Color4b,
        radius: Float,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        val gfx = currentGraphics
        if (gfx != null) {
            VulkanShapes.drawRoundedRect(gfx, x, y, width, height, color, radius)
            return
        }

        val r = color.r / 255f
        val g = color.g / 255f
        val b = color.b / 255f
        val a = color.a / 255f

        RectRenderer.emitDirect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            trR = r, trG = g, trB = b, trA = a,
            tlR = r, tlG = g, tlB = b, tlA = a,
            brR = r, brG = g, brB = b, brA = a,
            blR = r, blG = g, blB = b, blA = a,
            radiusTl = radius, radiusTr = radius, radiusBr = radius, radiusBl = radius,
            pipeline = pipeline
        )
    }

    fun roundedRectWithBorder(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Color4b,
        radius: Float,
        borderWidth: Float,
        borderColor: Color4b,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        val gfx = currentGraphics
        if (gfx != null) {
            VulkanShapes.drawRoundedRectWithBorder(gfx, x, y, width, height, color, radius, borderWidth, borderColor)
            return
        }

        val r = color.r / 255f
        val g = color.g / 255f
        val b = color.b / 255f
        val a = color.a / 255f

        val bcR = borderColor.r / 255f
        val bcG = borderColor.g / 255f
        val bcB = borderColor.b / 255f
        val bcA = borderColor.a / 255f

        RectRenderer.emitDirect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            trR = r, trG = g, trB = b, trA = a,
            tlR = r, tlG = g, tlB = b, tlA = a,
            brR = r, brG = g, brB = b, brA = a,
            blR = r, blG = g, blB = b, blA = a,
            radiusTl = radius, radiusTr = radius, radiusBr = radius, radiusBl = radius,
            borderWidth = borderWidth,
            bcR = bcR, bcG = bcG, bcB = bcB, bcA = bcA,
            pipeline = pipeline
        )
    }

    fun customRoundedRect(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Color4b,
        topLeft: Float,
        topRight: Float,
        bottomRight: Float,
        bottomLeft: Float,
        borderWidth: Float = 0f,
        borderColor: Color4b = Color4b.TRANSPARENT,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        val gfx = currentGraphics
        if (gfx != null) {
            VulkanShapes.drawCustom(
                gfx, x, y, width, height,
                topLeft, topRight, bottomRight, bottomLeft,
                color.argb, color.argb, color.argb, color.argb,
                borderWidth, borderColor.argb
            )
            return
        }

        val r = color.r / 255f
        val g = color.g / 255f
        val b = color.b / 255f
        val a = color.a / 255f

        val bcR = borderColor.r / 255f
        val bcG = borderColor.g / 255f
        val bcB = borderColor.b / 255f
        val bcA = borderColor.a / 255f

        RectRenderer.emitDirect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            trR = r, trG = g, trB = b, trA = a,
            tlR = r, tlG = g, tlB = b, tlA = a,
            brR = r, brG = g, brB = b, brA = a,
            blR = r, blG = g, blB = b, blA = a,
            radiusTl = topLeft, radiusTr = topRight, radiusBr = bottomRight, radiusBl = bottomLeft,
            borderWidth = borderWidth,
            bcR = bcR, bcG = bcG, bcB = bcB, bcA = bcA,
            pipeline = pipeline
        )
    }

    fun gradientRect(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        topLeft: Color4b,
        topRight: Color4b,
        bottomRight: Color4b,
        bottomLeft: Color4b,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        val gfx = currentGraphics
        if (gfx != null) {
            VulkanShapes.drawGradientRect(gfx, x, y, width, height, topLeft, topRight, bottomRight, bottomLeft)
            return
        }

        RectRenderer.emitDirect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            trR = topRight.r / 255f, trG = topRight.g / 255f, trB = topRight.b / 255f, trA = topRight.a / 255f,
            tlR = topLeft.r / 255f, tlG = topLeft.g / 255f, tlB = topLeft.b / 255f, tlA = topLeft.a / 255f,
            brR = bottomRight.r / 255f, brG = bottomRight.g / 255f, brB = bottomRight.b / 255f, brA = bottomRight.a / 255f,
            blR = bottomLeft.r / 255f, blG = bottomLeft.g / 255f, blB = bottomLeft.b / 255f, blA = bottomLeft.a / 255f,
            radiusTl = 0f, radiusTr = 0f, radiusBr = 0f, radiusBl = 0f,
            pipeline = pipeline
        )
    }

    fun texturedRect(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        textureId: Int,
        tint: Color4b = Color4b.WHITE,
        mix: Float = 0f,
        alpha: Float = 1f,
        uv: RectUv = RectUv.DEFAULT,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        val gfx = currentGraphics
        if (gfx != null) {
            return
        }

        val r = tint.r / 255f
        val g = tint.g / 255f
        val b = tint.b / 255f
        val a = tint.a / 255f

        RectRenderer.emitDirect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            trR = r, trG = g, trB = b, trA = a,
            tlR = r, tlG = g, tlB = b, tlA = a,
            brR = r, brG = g, brB = b, brA = a,
            blR = r, blG = g, blB = b, blA = a,
            radiusTl = 0f, radiusTr = 0f, radiusBr = 0f, radiusBl = 0f,
            mix = mix,
            alpha = alpha,
            typeId = 1f,
            textureId = textureId,
            u0 = uv.u0, v0 = uv.v0, u1 = uv.u1, v1 = uv.v1,
            pipeline = pipeline
        )
    }

    fun texturedRect(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        texture: GLTexture,
        tint: Color4b = Color4b.WHITE,
        mix: Float = 0f,
        alpha: Float = 1f,
        uv: RectUv = RectUv.DEFAULT,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        texturedRect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            textureId = texture.texId,
            tint = tint,
            mix = mix,
            alpha = alpha,
            uv = uv,
            pipeline = pipeline
        )
    }

    fun rect(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        style: RectStyle,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        val gfx = currentGraphics
        if (gfx != null) {
            VulkanShapes.drawRect(gfx, x, y, width, height, style)
            return
        }

        RectRenderer.emit(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            style = style,
            pipeline = pipeline
        )
    }

    fun rect(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        colors: RectColors,
        corners: RectCorners = RectCorners.ZERO,
        border: RectBorder = RectBorder.NONE,
        mode: Float = 0f,
        textureId: Int = 0,
        mix: Float = 0f,
        alpha: Float = 1f,
        uv: RectUv = RectUv.DEFAULT,
        type: RectType = RectType.BASIC,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        val gfx = currentGraphics
        if (gfx != null) {
            VulkanShapes.drawCustom(
                gfx, x, y, width, height,
                corners.topLeft, corners.topRight, corners.bottomRight, corners.bottomLeft,
                colors.topLeft.argb, colors.topRight.argb, colors.bottomRight.argb, colors.bottomLeft.argb,
                border.width, border.color.argb
            )
            return
        }

        RectRenderer.emitDirect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            trR = colors.topRight.r / 255f, trG = colors.topRight.g / 255f, trB = colors.topRight.b / 255f, trA = colors.topRight.a / 255f,
            tlR = colors.topLeft.r / 255f, tlG = colors.topLeft.g / 255f, tlB = colors.topLeft.b / 255f, tlA = colors.topLeft.a / 255f,
            brR = colors.bottomRight.r / 255f, brG = colors.bottomRight.g / 255f, brB = colors.bottomRight.b / 255f, brA = colors.bottomRight.a / 255f,
            blR = colors.bottomLeft.r / 255f, blG = colors.bottomLeft.g / 255f, blB = colors.bottomLeft.b / 255f, blA = colors.bottomLeft.a / 255f,
            radiusTl = corners.topLeft, radiusTr = corners.topRight, radiusBr = corners.bottomRight, radiusBl = corners.bottomLeft,
            mix = mix,
            alpha = alpha,
            mode = mode,
            borderWidth = border.width,
            bcR = border.color.r / 255f, bcG = border.color.g / 255f, bcB = border.color.b / 255f, bcA = border.color.a / 255f,
            typeId = type.id,
            textureId = textureId,
            u0 = uv.u0, v0 = uv.v0, u1 = uv.u1, v1 = uv.v1,
            pipeline = pipeline
        )
    }
}
