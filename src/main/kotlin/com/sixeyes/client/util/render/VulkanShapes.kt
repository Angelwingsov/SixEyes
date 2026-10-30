package com.sixeyes.client.util.render

import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.renderpearl.api.pipeline.RenderPipeline
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.rect.RectCorners
import com.sixeyes.client.util.render.rect.RectStyle
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.renderer.state.gui.GuiElementRenderState
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.resources.Identifier
import org.joml.Matrix3x2fc
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class VulkanQuadRenderState(
    private val pipeline: RenderPipeline,
    pose: Matrix3x2fc,
    private val x1: Float,
    private val y1: Float,
    private val x2: Float,
    private val y2: Float,
    private val cTL: Int,
    private val cTR: Int,
    private val cBR: Int,
    private val cBL: Int,
    private val scissor: ScreenRectangle?
) : GuiElementRenderState {
    private val m00 = pose.m00()
    private val m01 = pose.m01()
    private val m10 = pose.m10()
    private val m11 = pose.m11()
    private val m20 = pose.m20()
    private val m21 = pose.m21()

    private val boundsRect: ScreenRectangle? = computeBounds(
        m00, m01, m10, m11, m20, m21,
        x1, y1, x2, y2, scissor
    )

    override fun buildVertices(consumer: VertexConsumer) {
        val tx1 = m00 * x1 + m10 * y1 + m20
        val ty1 = m01 * x1 + m11 * y1 + m21
        val tx2 = m00 * x2 + m10 * y2 + m20
        val ty2 = m01 * x2 + m11 * y2 + m21

        consumer.addVertex(tx1, ty1, 0f).setColor(cTL)
        consumer.addVertex(tx1, ty2, 0f).setColor(cBL)
        consumer.addVertex(tx2, ty2, 0f).setColor(cBR)
        consumer.addVertex(tx2, ty1, 0f).setColor(cTR)
    }

    override fun pipeline(): RenderPipeline = pipeline
    override fun textureSetup(): TextureSetup = TextureSetup.noTexture()
    override fun scissorArea(): ScreenRectangle? = scissor
    override fun bounds(): ScreenRectangle? = boundsRect

    companion object {
        fun computeBounds(
            m00: Float, m01: Float, m10: Float, m11: Float, m20: Float, m21: Float,
            x1: Float, y1: Float, x2: Float, y2: Float,
            scissor: ScreenRectangle?
        ): ScreenRectangle? {
            val minX = floor(minOf(x1, x2)).toInt()
            val minY = floor(minOf(y1, y2)).toInt()
            val maxX = ceil(maxOf(x1, x2)).toInt()
            val maxY = ceil(maxOf(y1, y2)).toInt()

            val tX1 = floor(m00 * minX + m10 * minY + m20).toInt()
            val tY1 = floor(m01 * minX + m11 * minY + m21).toInt()
            val tX2 = ceil(m00 * maxX + m10 * maxY + m20).toInt()
            val tY2 = ceil(m01 * maxX + m11 * maxY + m21).toInt()

            val bX1 = minOf(tX1, tX2)
            val bY1 = minOf(tY1, tY2)
            val bX2 = maxOf(tX1, tX2)
            val bY2 = maxOf(tY1, tY2)

            val fX1 = if (scissor != null) maxOf(bX1, scissor.left()) else bX1
            val fY1 = if (scissor != null) maxOf(bY1, scissor.top()) else bY1
            val fX2 = if (scissor != null) minOf(bX2, scissor.right()) else bX2
            val fY2 = if (scissor != null) minOf(bY2, scissor.bottom()) else bY2

            val w = fX2 - fX1
            val h = fY2 - fY1
            if (w <= 0 || h <= 0) {
                return ScreenRectangle.empty()
            }
            return ScreenRectangle(fX1, fY1, w, h)
        }
    }
}

class VulkanRectSdfRenderState(
    pose: Matrix3x2fc,
    private val x: Float,
    private val y: Float,
    private val width: Float,
    private val height: Float,
    private val rTL: Float,
    private val rTR: Float,
    private val rBR: Float,
    private val rBL: Float,
    private val cTL: Int,
    private val cTR: Int,
    private val cBR: Int,
    private val cBL: Int,
    private val borderWidth: Float,
    private val borderColor: Int,
    private val scissor: ScreenRectangle?
) : GuiElementRenderState {
    private val m00 = pose.m00()
    private val m01 = pose.m01()
    private val m10 = pose.m10()
    private val m11 = pose.m11()
    private val m20 = pose.m20()
    private val m21 = pose.m21()

    private val pad = 1.5f
    private val boundsRect: ScreenRectangle? = VulkanQuadRenderState.computeBounds(
        m00, m01, m10, m11, m20, m21,
        x - pad, y - pad, x + width + pad, y + height + pad,
        scissor
    )

    override fun buildVertices(consumer: VertexConsumer) {
        val maxR = minOf(width, height) * 0.5f
        val sTL = (rTL.coerceIn(0f, maxR) * 64f).roundToInt().coerceIn(0, 32767)
        val sTR = (rTR.coerceIn(0f, maxR) * 64f).roundToInt().coerceIn(0, 32767)
        val sBR = (rBR.coerceIn(0f, maxR) * 64f).roundToInt().coerceIn(0, 32767)
        val sBL = (rBL.coerceIn(0f, maxR) * 64f).roundToInt().coerceIn(0, 32767)

        val bcA = if (borderWidth > 0f) (borderColor ushr 24) and 0xFF else 0
        val bcR = (borderColor ushr 16) and 0xFF
        val bcG = (borderColor ushr 8) and 0xFF
        val bcB = borderColor and 0xFF

        val packedBorderRgb = ((bcR shl 16) or (bcG shl 8) or bcB).toFloat()
        val bwFixed = if (bcA > 0) (borderWidth.coerceAtLeast(0f) * 64f).roundToInt().coerceIn(0, 65535) else 0
        val packedBorderWA = ((bwFixed shl 8) or bcA).toFloat()

        val vx0 = x - pad
        val vy0 = y - pad
        val vx1 = x + width + pad
        val vy1 = y + height + pad

        val lx0 = -pad
        val ly0 = -pad
        val lx1 = width + pad
        val ly1 = height + pad

        emitVertex(consumer, vx0, vy0, lx0, ly0, cTL, sTL, sTR, sBR, sBL, packedBorderRgb, packedBorderWA)
        emitVertex(consumer, vx0, vy1, lx0, ly1, cBL, sTL, sTR, sBR, sBL, packedBorderRgb, packedBorderWA)
        emitVertex(consumer, vx1, vy1, lx1, ly1, cBR, sTL, sTR, sBR, sBL, packedBorderRgb, packedBorderWA)
        emitVertex(consumer, vx1, vy0, lx1, ly0, cTR, sTL, sTR, sBR, sBL, packedBorderRgb, packedBorderWA)
    }

    private fun emitVertex(
        consumer: VertexConsumer,
        vx: Float, vy: Float,
        lx: Float, ly: Float,
        color: Int,
        sTL: Int, sTR: Int, sBR: Int, sBL: Int,
        packedBorderRgb: Float,
        packedBorderWA: Float
    ) {
        val tx = m00 * vx + m10 * vy + m20
        val ty = m01 * vx + m11 * vy + m21
        consumer.addVertex(tx, ty, packedBorderRgb)
            .setColor(color)
            .setUv(lx, ly)
            .setUv1(sTL, sTR)
            .setUv2(sBR, sBL)
            .setUv3(width, height)
            .setLineWidth(packedBorderWA)
    }

    override fun pipeline(): RenderPipeline = VulkanShapePipelines.RECT_SDF
    override fun textureSetup(): TextureSetup = TextureSetup.noTexture()
    override fun scissorArea(): ScreenRectangle? = scissor
    override fun bounds(): ScreenRectangle? = boundsRect
}

object VulkanShapes {
    val whiteTextureId: Identifier by lazy {
        val identifier = Identifier.fromNamespaceAndPath("sixeyes", "textures/gui/white.png")
        val image = NativeImage(NativeImage.Format.RGBA, 1, 1, false)
        image.setPixel(0, 0, -1) // 0xFFFFFFFF
        val dynamicTexture = DynamicTexture({ "sixeyes_white" }, image)
        Minecraft.getInstance().textureManager.register(identifier, dynamicTexture)
        identifier
    }

    private fun multiplyAlpha(argb: Int, alphaMul: Float): Int {
        if (alphaMul >= 0.999f) return argb
        val a = ((argb ushr 24) and 0xFF)
        val na = (a * alphaMul.coerceIn(0f, 1f)).roundToInt().coerceIn(0, 255)
        return (na shl 24) or (argb and 0x00FFFFFF)
    }

    fun drawCustom(
        gfx: GuiGraphicsExtractor,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        rTL: Float,
        rTR: Float,
        rBR: Float,
        rBL: Float,
        cTL: Int,
        cTR: Int,
        cBR: Int,
        cBL: Int,
        borderWidth: Float = 0f,
        borderColor: Int = 0
    ) {
        if (width <= 0f || height <= 0f) return

        gfx.guiRenderState.addGuiElement(
            VulkanRectSdfRenderState(
                pose = gfx.pose(),
                x = x,
                y = y,
                width = width,
                height = height,
                rTL = rTL,
                rTR = rTR,
                rBR = rBR,
                rBL = rBL,
                cTL = cTL,
                cTR = cTR,
                cBR = cBR,
                cBL = cBL,
                borderWidth = borderWidth,
                borderColor = borderColor,
                scissor = gfx.scissorStack.peek()
            )
        )
    }

    fun drawRect(
        gfx: GuiGraphicsExtractor,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Color4b
    ) {
        val argb = color.argb
        drawCustom(gfx, x, y, width, height, 0f, 0f, 0f, 0f, argb, argb, argb, argb)
    }

    fun drawRoundedRect(
        gfx: GuiGraphicsExtractor,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Color4b,
        radius: Float
    ) {
        val argb = color.argb
        drawCustom(gfx, x, y, width, height, radius, radius, radius, radius, argb, argb, argb, argb)
    }

    fun drawRoundedRectWithBorder(
        gfx: GuiGraphicsExtractor,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Color4b,
        radius: Float,
        borderWidth: Float,
        borderColor: Color4b
    ) {
        val argb = color.argb
        drawCustom(
            gfx, x, y, width, height,
            radius, radius, radius, radius,
            argb, argb, argb, argb,
            borderWidth, borderColor.argb
        )
    }

    fun drawGradientRect(
        gfx: GuiGraphicsExtractor,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        cTL: Color4b,
        cTR: Color4b,
        cBR: Color4b,
        cBL: Color4b
    ) {
        drawCustom(gfx, x, y, width, height, 0f, 0f, 0f, 0f, cTL.argb, cTR.argb, cBR.argb, cBL.argb)
    }

    fun drawRect(
        gfx: GuiGraphicsExtractor,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        style: RectStyle
    ) {
        if (width <= 0f || height <= 0f) return
        val alphaMul = style.alpha.coerceIn(0f, 1f)
        if (alphaMul <= 0f) return

        val colors = style.colors
        val corners = style.corners
        val border = style.border

        val cTL = multiplyAlpha(colors.topLeft.argb, alphaMul)
        val cTR = multiplyAlpha(colors.topRight.argb, alphaMul)
        val cBR = multiplyAlpha(colors.bottomRight.argb, alphaMul)
        val cBL = multiplyAlpha(colors.bottomLeft.argb, alphaMul)

        val hasBorder = border.width > 0f && border.color.a > 0
        val bw = if (hasBorder) border.width else 0f
        val bc = if (hasBorder) multiplyAlpha(border.color.argb, alphaMul) else 0

        drawCustom(
            gfx, x, y, width, height,
            corners.topLeft, corners.topRight, corners.bottomRight, corners.bottomLeft,
            cTL, cTR, cBR, cBL,
            bw, bc
        )
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
        drawCustom(
            gfx, x1, y1, x2 - x1, y2 - y1,
            corners.topLeft, corners.topRight, corners.bottomRight, corners.bottomLeft,
            color, color, color, color
        )
    }

    fun drawRoundedRect(
        gfx: GuiGraphicsExtractor,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        corners: RectCorners,
        cTL: Int,
        cTR: Int,
        cBR: Int,
        cBL: Int,
        borderWidth: Float = 0f,
        borderColor: Int = 0
    ) {
        drawCustom(
            gfx, x1, y1, x2 - x1, y2 - y1,
            corners.topLeft, corners.topRight, corners.bottomRight, corners.bottomLeft,
            cTL, cTR, cBR, cBL,
            borderWidth, borderColor
        )
    }
}
