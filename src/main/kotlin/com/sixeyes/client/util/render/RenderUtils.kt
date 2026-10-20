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

    private fun renderVulkan(
        gfx: GuiGraphicsExtractor,
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        style: RectStyle
    ) {
        if (width <= 0f || height <= 0f) return
        val x1 = x
        val y1 = y
        val x2 = x + width
        val y2 = y + height

        val colors = style.colors
        val isSolid = colors.topLeft == colors.topRight && colors.topLeft == colors.bottomRight && colors.topLeft == colors.bottomLeft
        val hasBorder = style.border.width > 0f && style.border.color.a > 0
        val bw = if (hasBorder) style.border.width.coerceAtLeast(1f) else 0f

        val corners = style.corners
        val hasRoundedCorners = corners.topLeft > 0.5f || corners.topRight > 0.5f ||
                                corners.bottomLeft > 0.5f || corners.bottomRight > 0.5f

        if (isSolid) {
            val bodyColor = colors.topLeft.argb
            val bodyAlpha = colors.topLeft.a

            if (hasBorder) {
                val borderColor = style.border.color.argb
                if (hasRoundedCorners) {
                    VulkanShapes.drawRoundedRect(gfx, x1, y1, x2, y2, corners, borderColor)
                    if (bodyAlpha > 0) {
                        val innerCorners = RectCorners(
                            (corners.topLeft - bw).coerceAtLeast(0f),
                            (corners.topRight - bw).coerceAtLeast(0f),
                            (corners.bottomRight - bw).coerceAtLeast(0f),
                            (corners.bottomLeft - bw).coerceAtLeast(0f)
                        )
                        val ix1 = x1 + bw
                        val iy1 = y1 + bw
                        val ix2 = x2 - bw
                        val iy2 = y2 - bw
                        if (ix2 > ix1 && iy2 > iy1) {
                            VulkanShapes.drawRoundedRect(gfx, ix1, iy1, ix2, iy2, innerCorners, bodyColor)
                        }
                    }
                } else {
                    if (bodyAlpha > 0) {
                        gfx.fill(Math.round(x1), Math.round(y1), Math.round(x2), Math.round(y2), bodyColor)
                    }
                    gfx.outline(Math.round(x1), Math.round(y1), Math.round(x2 - x1), Math.round(y2 - y1), borderColor)
                }
            } else {
                if (bodyAlpha > 0) {
                    if (hasRoundedCorners) {
                        VulkanShapes.drawRoundedRect(gfx, x1, y1, x2, y2, corners, bodyColor)
                    } else {
                        gfx.fill(Math.round(x1), Math.round(y1), Math.round(x2), Math.round(y2), bodyColor)
                    }
                }
            }
        } else if (colors.topLeft != colors.topRight && colors.topLeft == colors.bottomLeft) {
            val ix1 = Math.round(x1)
            val iy1 = Math.round(y1)
            val ix2 = Math.round(x2)
            val iy2 = Math.round(y2)
            val steps = (ix2 - ix1).coerceIn(1, 48)
            val stepW = (ix2 - ix1).toFloat() / steps
            val c1 = colors.topLeft
            val c2 = colors.topRight
            for (s in 0 until steps) {
                val t = s.toDouble() / steps
                val curColor = c1.interpolateTo(c2, t).argb
                val sx1 = Math.round(ix1 + s * stepW)
                val sx2 = Math.round(ix1 + (s + 1) * stepW)
                if (sx2 > sx1) {
                    gfx.fill(sx1, iy1, sx2, iy2, curColor)
                }
            }
            if (hasBorder) {
                gfx.outline(ix1, iy1, ix2 - ix1, iy2 - iy1, style.border.color.argb)
            }
        } else {
            val ix1 = Math.round(x1)
            val iy1 = Math.round(y1)
            val ix2 = Math.round(x2)
            val iy2 = Math.round(y2)
            gfx.fillGradient(ix1, iy1, ix2, iy2, colors.topLeft.argb, colors.bottomLeft.argb)
            if (hasBorder) {
                gfx.outline(ix1, iy1, ix2 - ix1, iy2 - iy1, style.border.color.argb)
            }
        }
    }

    fun rect(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Color4b,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        rect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            style = RectStyle.solid(color),
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
        rect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            style = RectStyle.rounded(color, radius),
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
        rect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            style = RectStyle.gradient(topLeft, topRight, bottomRight, bottomLeft),
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
        rect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            style = RectStyle.textured(
                textureId = textureId,
                tint = tint,
                mix = mix,
                alpha = alpha,
                uv = uv
            ),
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
            renderVulkan(gfx, matrix, x, y, width, height, style)
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
        rect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            style = RectStyle(
                colors = colors,
                corners = corners,
                border = border,
                mode = mode,
                textureId = textureId,
                mix = mix,
                alpha = alpha,
                uv = uv,
                type = type
            ),
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
        rect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            colors = RectColors.solid(color),
            corners = RectCorners.all(radius),
            border = if (borderWidth > 0f) RectBorder(borderWidth, borderColor) else RectBorder.NONE,
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
        rect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            colors = RectColors.solid(color),
            corners = RectCorners(topLeft, topRight, bottomRight, bottomLeft),
            border = if (borderWidth > 0f) RectBorder(borderWidth, borderColor) else RectBorder.NONE,
            pipeline = pipeline
        )
    }
}
