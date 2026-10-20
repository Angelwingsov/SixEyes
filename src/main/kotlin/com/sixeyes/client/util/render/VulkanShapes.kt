package com.sixeyes.client.util.render

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier
import com.sixeyes.client.util.render.rect.RectCorners
import kotlin.math.hypot

object VulkanShapes {
    private const val TEX_SIZE = 128

    val cornerTextureId: Identifier by lazy {
        val identifier = Identifier.fromNamespaceAndPath("sixeyes", "textures/gui/corner.png")
        val image = NativeImage(NativeImage.Format.RGBA, TEX_SIZE, TEX_SIZE, false)

        val center = (TEX_SIZE - 0.5f)
        val radius = TEX_SIZE - 1.5f
        val edgeWidth = 2.0f

        for (y in 0 until TEX_SIZE) {
            val dy = center - y
            for (x in 0 until TEX_SIZE) {
                val dx = center - x
                val dist = hypot(dx, dy) - radius
                val t = ((dist + edgeWidth * 0.5f) / edgeWidth).coerceIn(0.0f, 1.0f)
                val alpha = 1.0f - t * t * (3.0f - 2.0f * t)
                val aInt = (alpha * 255.0f + 0.5f).toInt().coerceIn(0, 255)
                val argb = (aInt shl 24) or 0x00FFFFFF
                image.setPixel(x, y, argb)
            }
        }

        val texture = LinearDynamicTexture({ "sixeyes_shape_corner" }, image)
        Minecraft.getInstance().textureManager.register(identifier, texture)
        identifier
    }

    fun drawRoundedRect(
        gfx: GuiGraphicsExtractor,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        corners: RectCorners,
        color: Int
    ) {
        val w = x2 - x1
        val h = y2 - y1
        if (w <= 0f || h <= 0f) return

        val maxR = minOf(w, h) * 0.5f
        val rTL = corners.topLeft.coerceIn(0f, maxR)
        val rTR = corners.topRight.coerceIn(0f, maxR)
        val rBL = corners.bottomLeft.coerceIn(0f, maxR)
        val rBR = corners.bottomRight.coerceIn(0f, maxR)

        if (rTL <= 0.5f && rTR <= 0.5f && rBL <= 0.5f && rBR <= 0.5f) {
            gfx.fill(Math.round(x1), Math.round(y1), Math.round(x2), Math.round(y2), color)
            return
        }

        val cornerId = cornerTextureId
        val pose = gfx.pose()
        pose.pushMatrix()

        val invScale = 8f
        val subScale = 0.125f
        pose.scale(subScale, subScale)

        try {
            val sx1 = Math.round(x1 * invScale)
            val sy1 = Math.round(y1 * invScale)
            val sx2 = Math.round(x2 * invScale)
            val sy2 = Math.round(y2 * invScale)

            val sTL = Math.round(rTL * invScale)
            val sTR = Math.round(rTR * invScale)
            val sBL = Math.round(rBL * invScale)
            val sBR = Math.round(rBR * invScale)

            if (sTL > 0) {
                gfx.innerBlit(
                    RenderPipelines.GUI_TEXTURED,
                    cornerId,
                    sx1, sx1 + sTL, sy1, sy1 + sTL,
                    0f, 1f, 0f, 1f,
                    color
                )
            }
            if (sTR > 0) {
                gfx.innerBlit(
                    RenderPipelines.GUI_TEXTURED,
                    cornerId,
                    sx2 - sTR, sx2, sy1, sy1 + sTR,
                    1f, 0f, 0f, 1f,
                    color
                )
            }
            if (sBR > 0) {
                gfx.innerBlit(
                    RenderPipelines.GUI_TEXTURED,
                    cornerId,
                    sx2 - sBR, sx2, sy2 - sBR, sy2,
                    1f, 0f, 1f, 0f,
                    color
                )
            }
            if (sBL > 0) {
                gfx.innerBlit(
                    RenderPipelines.GUI_TEXTURED,
                    cornerId,
                    sx1, sx1 + sBL, sy2 - sBL, sy2,
                    0f, 1f, 1f, 0f,
                    color
                )
            }

            val topH = maxOf(sTL, sTR)
            val botH = maxOf(sBL, sBR)

            if (sx2 - sTR > sx1 + sTL) {
                gfx.fill(sx1 + sTL, sy1, sx2 - sTR, sy1 + topH, color)
            }
            if (sTL > sTR && sTR > 0) {
                gfx.fill(sx2 - sTR, sy1 + sTR, sx2, sy1 + sTL, color)
            } else if (sTR > sTL && sTL > 0) {
                gfx.fill(sx1, sy1 + sTL, sx1 + sTL, sy1 + sTR, color)
            }

            val midY1 = sy1 + topH
            val midY2 = sy2 - botH
            if (midY2 > midY1) {
                gfx.fill(sx1, midY1, sx2, midY2, color)
            }

            if (sx2 - sBR > sx1 + sBL) {
                gfx.fill(sx1 + sBL, sy2 - botH, sx2 - sBR, sy2, color)
            }
            if (sBL > sBR && sBR > 0) {
                gfx.fill(sx2 - sBR, sy2 - sBL, sx2, sy2 - sBR, color)
            } else if (sBR > sBL && sBL > 0) {
                gfx.fill(sx1, sy2 - sBR, sx1 + sBL, sy2 - sBL, color)
            }
        } finally {
            pose.popMatrix()
        }
    }
}
