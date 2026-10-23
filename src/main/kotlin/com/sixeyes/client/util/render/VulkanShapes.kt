package com.sixeyes.client.util.render

import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.renderpearl.api.pipeline.RenderPipeline
import com.sixeyes.client.util.render.rect.RectCorners
import com.sixeyes.client.util.render.rect.RectStyle
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.renderer.state.gui.GuiElementRenderState
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.resources.Identifier
import org.joml.Matrix3x2f
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
    private val pose: Matrix3x2f = Matrix3x2f(pose)
    private val boundsRect: ScreenRectangle? = computeBounds(this.pose, x1, y1, x2, y2, scissor)

    override fun buildVertices(consumer: VertexConsumer) {
        consumer.addVertexWith2DPose(pose, x1, y1).setColor(cTL)
        consumer.addVertexWith2DPose(pose, x1, y2).setColor(cBL)
        consumer.addVertexWith2DPose(pose, x2, y2).setColor(cBR)
        consumer.addVertexWith2DPose(pose, x2, y1).setColor(cTR)
    }

    override fun pipeline(): RenderPipeline = pipeline
    override fun textureSetup(): TextureSetup = TextureSetup.noTexture()
    override fun scissorArea(): ScreenRectangle? = scissor
    override fun bounds(): ScreenRectangle? = boundsRect

    companion object {
        fun computeBounds(
            pose: Matrix3x2fc,
            x1: Float,
            y1: Float,
            x2: Float,
            y2: Float,
            scissor: ScreenRectangle?
        ): ScreenRectangle? {
            val minX = floor(minOf(x1, x2)).toInt()
            val minY = floor(minOf(y1, y2)).toInt()
            val maxX = ceil(maxOf(x1, x2)).toInt()
            val maxY = ceil(maxOf(y1, y2)).toInt()
            val raw = ScreenRectangle(minX, minY, maxOf(1, maxX - minX), maxOf(1, maxY - minY))
                .transformMaxBounds(pose)
            return if (scissor != null) scissor.intersection(raw) else raw
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
    private val pose: Matrix3x2f = Matrix3x2f(pose)
    private val pad = 1.5f
    private val boundsRect: ScreenRectangle? = VulkanQuadRenderState.computeBounds(
        this.pose,
        x - pad,
        y - pad,
        x + width + pad,
        y + height + pad,
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
        vx: Float,
        vy: Float,
        lx: Float,
        ly: Float,
        color: Int,
        sTL: Int,
        sTR: Int,
        sBR: Int,
        sBL: Int,
        packedBorderRgb: Float,
        packedBorderWA: Float
    ) {
        val tx = pose.m00() * vx + pose.m10() * vy + pose.m20()
        val ty = pose.m01() * vx + pose.m11() * vy + pose.m21()
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

        gfx.guiRenderState.addGuiElement(
            VulkanRectSdfRenderState(
                pose = gfx.pose(),
                x = x,
                y = y,
                width = width,
                height = height,
                rTL = corners.topLeft,
                rTR = corners.topRight,
                rBR = corners.bottomRight,
                rBL = corners.bottomLeft,
                cTL = cTL,
                cTR = cTR,
                cBR = cBR,
                cBL = cBL,
                borderWidth = bw,
                borderColor = bc,
                scissor = gfx.scissorStack.peek()
            )
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
        drawRoundedRect(gfx, x1, y1, x2, y2, corners, color, color, color, color)
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
        val w = x2 - x1
        val h = y2 - y1
        if (w <= 0f || h <= 0f) return

        gfx.guiRenderState.addGuiElement(
            VulkanRectSdfRenderState(
                pose = gfx.pose(),
                x = x1,
                y = y1,
                width = w,
                height = h,
                rTL = corners.topLeft,
                rTR = corners.topRight,
                rBR = corners.bottomRight,
                rBL = corners.bottomLeft,
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
}
