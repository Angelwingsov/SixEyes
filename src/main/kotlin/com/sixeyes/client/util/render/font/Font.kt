package com.sixeyes.client.util.render.font

import com.mojang.renderpearl.api.pipeline.RenderPipeline
import com.sixeyes.client.render.main.vertex.mesh.MeshBuilder
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.other.color.ColorUtil
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.ScissorUtil
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.resources.Identifier
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.engine.controls.LayerControl
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.system.MemoryUtil

class Font(
    val name: String,
    val lineHeight: Float,
    val ascender: Float,
    val descender: Float,
    private val ascii: Array<Glyph?>,
    private val glyphs: Int2ObjectOpenHashMap<Glyph>,
    private val fallback: Glyph?,
    internal val batch: MsdfBatchState,
    val pngPath: String = "",
    val distanceRange: Float = 8f
) {
    val vulkanAtlasId: Identifier by lazy {
        VulkanFontAtlas.getOrRegister(name, pngPath)
    }
    val vulkanPipeline: RenderPipeline by lazy {
        VulkanFontPipelines.getPipeline(distanceRange)
    }

    class Glyph(
        val advance: Float,
        val u0: Float,
        val v0: Float,
        val u1: Float,
        val v1: Float,
        val x0: Float,
        val y0: Float,
        val x1: Float,
        val y1: Float,
        val empty: Boolean
    )

    fun glyphFor(code: Int): Glyph? =
        (if (code in 0..127) ascii[code] else glyphs.get(code)) ?: fallback

    fun advance(code: Int): Float = glyphFor(code)?.advance ?: 0f

    fun advance(c: Char): Float = advance(c.code)

    fun getWidth(text: CharSequence, size: Float): Float {
        if (text.isEmpty() || size <= 0f) return 0f
        var maxWidth = 0f
        var lineWidth = 0f
        var i = 0
        val len = text.length
        while (i < len) {
            when (val c = text[i++]) {
                '§' -> if (i < len) i++
                '\n' -> {
                    maxWidth = maxOf(maxWidth, lineWidth)
                    lineWidth = 0f
                }
                '\r' -> continue
                else -> lineWidth += advance(c)
            }
        }
        return maxOf(maxWidth, lineWidth) * size
    }

    fun width(text: CharSequence, size: Float): Float = getWidth(text, size)

    fun getHeight(size: Float): Float = ascender * size

    fun height(size: Float): Float = getHeight(size)

    fun getHeight(text: CharSequence, size: Float): Float {
        if (text.isEmpty() || size <= 0f) return 0f
        var lines = 1
        for (i in 0 until text.length) {
            if (text[i] == '\n') lines++
        }
        return (ascender + (lines - 1) * lineHeight) * size
    }

    fun height(text: CharSequence, size: Float): Float = getHeight(text, size)

    fun getLineHeight(size: Float): Float = lineHeight * size

    fun getAscender(size: Float): Float = ascender * size

    fun getDescender(size: Float): Float = descender * size

    @JvmOverloads
    fun drawString(
        matrix: Matrix4f,
        text: CharSequence,
        x: Float,
        y: Float,
        size: Float,
        color: Color4b = Color4b.WHITE,
        shadow: Boolean = false,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_TEXT,
        scissor: Vector4f? = null
    ): Float {
        if (text.isEmpty() || size <= 0f) return 0f

        val gfx = RenderUtils.currentGraphics
        if (gfx != null) {
            if (shadow) {
                val offset = maxOf(0.75f, size * 0.05f)
                renderVulkanPass(gfx, text, x + offset, y + offset, size, color, true)
            }
            return renderVulkanPass(gfx, text, x, y, size, color, false)
        }

        val builder = LayerControl.getBuilder(pipeline, MsdfTextRenderer, batch) ?: return 0f
        val sc = scissor ?: ScissorUtil.getCurrentScissorValues()
        val sx = sc.x
        val sy = sc.y
        val sz = sc.z
        val sw = sc.w

        if (shadow) {
            val offset = maxOf(0.75f, size * 0.05f)
            renderPass(builder, matrix, text, x + offset, y + offset, size, color, true, sx, sy, sz, sw)
        }
        return renderPass(builder, matrix, text, x, y, size, color, false, sx, sy, sz, sw)
    }

    @JvmOverloads
    fun drawCenteredString(
        matrix: Matrix4f,
        text: CharSequence,
        centerX: Float,
        y: Float,
        size: Float,
        color: Color4b = Color4b.WHITE,
        shadow: Boolean = false,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_TEXT,
        scissor: Vector4f? = null
    ): Float {
        val w = getWidth(text, size)
        drawString(matrix, text, centerX - w * 0.5f, y, size, color, shadow, pipeline, scissor)
        return w
    }

    @JvmOverloads
    fun drawRightString(
        matrix: Matrix4f,
        text: CharSequence,
        rightX: Float,
        y: Float,
        size: Float,
        color: Color4b = Color4b.WHITE,
        shadow: Boolean = false,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_TEXT,
        scissor: Vector4f? = null
    ): Float {
        val w = getWidth(text, size)
        drawString(matrix, text, rightX - w, y, size, color, shadow, pipeline, scissor)
        return w
    }

    private fun formatColor(code: Char): Int = when (code) {
        in '0'..'9' -> ColorUtil.hexColors[code - '0']
        in 'a'..'f' -> ColorUtil.hexColors[code - 'a' + 10]
        in 'A'..'F' -> ColorUtil.hexColors[code - 'A' + 10]
        else -> -1
    }

    private fun renderVulkanPass(
        gfx: GuiGraphicsExtractor,
        text: CharSequence,
        x: Float,
        y: Float,
        size: Float,
        color: Color4b,
        isShadow: Boolean
    ): Float {
        val pipeline = vulkanPipeline
        val atlasId = vulkanAtlasId
        var penX = x
        var baselineY = y + ascender * size
        var maxWidth = 0f
        val shadowFactor = if (isShadow) 0.25f else 1.0f

        val baseAlpha = color.a.coerceIn(0, 255)
        var curPackedArgb = 0

        fun updatePackedColor(rgb: Int) {
            val r = (((rgb shr 16 and 0xFF).toFloat() * shadowFactor) + 0.5f).toInt().coerceIn(0, 255)
            val g = (((rgb shr 8 and 0xFF).toFloat() * shadowFactor) + 0.5f).toInt().coerceIn(0, 255)
            val b = (((rgb and 0xFF).toFloat() * shadowFactor) + 0.5f).toInt().coerceIn(0, 255)
            curPackedArgb = (baseAlpha shl 24) or (r shl 16) or (g shl 8) or b
        }

        updatePackedColor(color.argb)

        val pose = gfx.pose()
        pose.pushMatrix()
        val invScale = 32f
        val subScale = 1f / invScale
        pose.scale(subScale, subScale)

        try {
            var i = 0
            val len = text.length
            while (i < len) {
                val c = text[i++]
                when (c) {
                    '§' -> {
                        if (i >= len) break
                        val code = text[i++]
                        if (code == 'r' || code == 'R') {
                            updatePackedColor(color.argb)
                        } else {
                            val rgb = formatColor(code)
                            if (rgb != -1) updatePackedColor(rgb)
                        }
                    }
                    '\n' -> {
                        maxWidth = maxOf(maxWidth, penX - x)
                        penX = x
                        baselineY += lineHeight * size
                    }
                    '\r' -> {}
                    else -> {
                        val glyph = glyphFor(c.code) ?: continue
                        if (!glyph.empty) {
                            val gx0 = Math.round((penX + glyph.x0 * size) * invScale)
                            var gx1 = Math.round((penX + glyph.x1 * size) * invScale)
                            val gy0 = Math.round((baselineY - glyph.y0 * size) * invScale)
                            var gy1 = Math.round((baselineY - glyph.y1 * size) * invScale)
                            if (gx1 <= gx0) gx1 = gx0 + 1
                            if (gy1 <= gy0) gy1 = gy0 + 1

                            gfx.innerBlit(
                                pipeline,
                                atlasId,
                                gx0, gx1, gy0, gy1,
                                glyph.u0, glyph.u1, glyph.v0, glyph.v1,
                                curPackedArgb
                            )
                        }
                        penX += glyph.advance * size
                    }
                }
            }
        } finally {
            pose.popMatrix()
        }
        return maxOf(maxWidth, penX - x)
    }

    private fun renderPass(
        builder: MeshBuilder,
        matrix: Matrix4f,
        text: CharSequence,
        x: Float,
        y: Float,
        size: Float,
        color: Color4b,
        isShadow: Boolean,
        sx: Float, sy: Float, sz: Float, sw: Float
    ): Float {
        var penX = x
        var baselineY = y + ascender * size
        var maxWidth = 0f
        val alpha = color.a / 255f
        val shadowFactor = if (isShadow) 0.25f else 1.0f

        var r = 0f
        var g = 0f
        var b = 0f

        fun applyColor(rgb: Int) {
            r = ((rgb shr 16 and 0xFF) / 255f) * shadowFactor
            g = ((rgb shr 8 and 0xFF) / 255f) * shadowFactor
            b = ((rgb and 0xFF) / 255f) * shadowFactor
        }

        applyColor(color.argb)

        val m00 = matrix.m00()
        val m01 = matrix.m01()
        val m10 = matrix.m10()
        val m11 = matrix.m11()
        val m30 = matrix.m30()
        val m31 = matrix.m31()

        var i = 0
        val len = text.length
        while (i < len) {
            val c = text[i++]
            when (c) {
                '§' -> {
                    if (i >= len) break
                    val code = text[i++]
                    if (code == 'r' || code == 'R') {
                        applyColor(color.argb)
                    } else {
                        val rgb = formatColor(code)
                        if (rgb != -1) applyColor(rgb)
                    }
                }
                '\n' -> {
                    maxWidth = maxOf(maxWidth, penX - x)
                    penX = x
                    baselineY += lineHeight * size
                }
                '\r' -> {}
                else -> {
                    val glyph = glyphFor(c.code) ?: continue
                    if (!glyph.empty) {
                        val x0 = penX + glyph.x0 * size
                        val x1 = penX + glyph.x1 * size
                        val y0 = baselineY - glyph.y0 * size
                        val y1 = baselineY - glyph.y1 * size

                        val ptr = builder.reserveQuad()
                        if (ptr != -1L) {
                            writeGlyphDirect(
                                ptr,
                                m00, m01, m10, m11, m30, m31,
                                x0, y0, x1, y1,
                                glyph.u0, glyph.v0, glyph.u1, glyph.v1,
                                r, g, b, alpha,
                                sx, sy, sz, sw
                            )
                        }
                    }
                    penX += glyph.advance * size
                }
            }
        }
        return maxOf(maxWidth, penX - x)
    }

    private inline fun writeGlyphDirect(
        ptr: Long,
        m00: Float, m01: Float, m10: Float, m11: Float, m30: Float, m31: Float,
        x0: Float, y0: Float, x1: Float, y1: Float,
        u0: Float, v0: Float, u1: Float, v1: Float,
        r: Float, g: Float, bCol: Float, a: Float,
        sx: Float, sy: Float, sz: Float, sw: Float
    ) {
        val v0Ptr = ptr
        MemoryUtil.memPutFloat(v0Ptr, m00 * x0 + m10 * y0 + m30)
        MemoryUtil.memPutFloat(v0Ptr + 4L, m01 * x0 + m11 * y0 + m31)
        MemoryUtil.memPutFloat(v0Ptr + 8L, 0f)
        MemoryUtil.memPutFloat(v0Ptr + 12L, u0)
        MemoryUtil.memPutFloat(v0Ptr + 16L, v0)
        writeGlyphCommon(v0Ptr, r, g, bCol, a, sx, sy, sz, sw)

        val v1Ptr = ptr + 96L
        MemoryUtil.memPutFloat(v1Ptr, m00 * x0 + m10 * y1 + m30)
        MemoryUtil.memPutFloat(v1Ptr + 4L, m01 * x0 + m11 * y1 + m31)
        MemoryUtil.memPutFloat(v1Ptr + 8L, 0f)
        MemoryUtil.memPutFloat(v1Ptr + 12L, u0)
        MemoryUtil.memPutFloat(v1Ptr + 16L, v1)
        writeGlyphCommon(v1Ptr, r, g, bCol, a, sx, sy, sz, sw)

        val v2Ptr = ptr + 192L
        MemoryUtil.memPutFloat(v2Ptr, m00 * x1 + m10 * y1 + m30)
        MemoryUtil.memPutFloat(v2Ptr + 4L, m01 * x1 + m11 * y1 + m31)
        MemoryUtil.memPutFloat(v2Ptr + 8L, 0f)
        MemoryUtil.memPutFloat(v2Ptr + 12L, u1)
        MemoryUtil.memPutFloat(v2Ptr + 16L, v1)
        writeGlyphCommon(v2Ptr, r, g, bCol, a, sx, sy, sz, sw)

        val v3Ptr = ptr + 288L
        MemoryUtil.memPutFloat(v3Ptr, m00 * x1 + m10 * y0 + m30)
        MemoryUtil.memPutFloat(v3Ptr + 4L, m01 * x1 + m11 * y0 + m31)
        MemoryUtil.memPutFloat(v3Ptr + 8L, 0f)
        MemoryUtil.memPutFloat(v3Ptr + 12L, u1)
        MemoryUtil.memPutFloat(v3Ptr + 16L, v0)
        writeGlyphCommon(v3Ptr, r, g, bCol, a, sx, sy, sz, sw)
    }

    private inline fun writeGlyphCommon(
        v: Long,
        r: Float, g: Float, b: Float, alpha: Float,
        sx: Float, sy: Float, sz: Float, sw: Float
    ) {
        MemoryUtil.memPutFloat(v + 20L, r)
        MemoryUtil.memPutFloat(v + 24L, g)
        MemoryUtil.memPutFloat(v + 28L, b)
        MemoryUtil.memPutFloat(v + 32L, alpha)

        MemoryUtil.memPutFloat(v + 36L, 0f)
        MemoryUtil.memPutFloat(v + 40L, 0.5f)
        MemoryUtil.memPutFloat(v + 44L, 0f)

        MemoryUtil.memPutFloat(v + 48L, 0f)
        MemoryUtil.memPutFloat(v + 52L, 0f)
        MemoryUtil.memPutFloat(v + 56L, 0f)
        MemoryUtil.memPutFloat(v + 60L, 0f)

        MemoryUtil.memPutFloat(v + 64L, -1e5f)
        MemoryUtil.memPutFloat(v + 68L, 1e5f)
        MemoryUtil.memPutFloat(v + 72L, 0f)
        MemoryUtil.memPutFloat(v + 76L, 0f)

        MemoryUtil.memPutFloat(v + 80L, sx)
        MemoryUtil.memPutFloat(v + 84L, sy)
        MemoryUtil.memPutFloat(v + 88L, sz)
        MemoryUtil.memPutFloat(v + 92L, sw)
    }
}
