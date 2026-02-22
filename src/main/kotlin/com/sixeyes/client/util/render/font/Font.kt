package com.sixeyes.client.util.render.font

import com.sixeyes.client.render.main.vertex.mesh.MeshBuilder
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.engine.controls.LayerControl
import org.joml.Matrix4f

class Font internal constructor(
    private val atlasWidth: Float,
    private val atlasHeight: Float,
    private val atlasBottomOrigin: Boolean,
    private val lineHeight: Float,
    private val ascender: Float,
    private val glyphs: Map<Int, Glyph>,
    private val fallbackGlyph: Glyph?,
    private val batchState: MsdfBatchState
) {
    private val invAtlasWidth = 1f / atlasWidth
    private val invAtlasHeight = 1f / atlasHeight
    private val baselineOffset = ascender

    private val asciiGlyphs: Array<Glyph?> = Array(128) { glyphs[it] }

    fun drawString(
        matrix: Matrix4f,
        text: CharSequence,
        x: Float,
        y: Float,
        size: Float,
        color: Color4b,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_TEXT
    ) {
        if (text.isEmpty() || size <= 0f) return

        val builder = LayerControl.getBuilder(pipeline, MsdfTextRenderer, batchState) ?: return

        val r = color.r / 255f
        val g = color.g / 255f
        val b = color.b / 255f
        val a = color.a / 255f

        var penX = x
        var baselineY = y + baselineOffset * size

        for (i in text.indices) {
            val charCode = text[i].code
            if (charCode == 10) {
                penX = x
                baselineY += lineHeight * size
                continue
            }
            if (charCode == 13) continue

            val glyph = glyphFor(charCode) ?: continue
            val plane = glyph.plane
            val atlas = glyph.atlas

            if (plane != null && atlas != null) {
                emitGlyphQuad(builder, matrix, penX, baselineY, size, plane, atlas, r, g, b, a)
            }

            penX += glyph.advance * size
        }
    }

    private fun glyphFor(charCode: Int): Glyph? {
        return if (charCode in 0..127) asciiGlyphs[charCode] ?: fallbackGlyph
        else glyphs[charCode] ?: fallbackGlyph
    }

    private fun emitGlyphQuad(builder: MeshBuilder, matrix: Matrix4f, penX: Float, baselineY: Float, size: Float, plane: PlaneBounds, atlas: AtlasBounds, r: Float, g: Float, b: Float, a: Float) {
        val left = penX + plane.left * size
        val right = penX + plane.right * size
        val top = baselineY - plane.top * size
        val bottom = baselineY - plane.bottom * size

        val u0 = atlas.left * invAtlasWidth
        val u1 = atlas.right * invAtlasWidth
        val vTop = if (atlasBottomOrigin) 1f - atlas.top * invAtlasHeight else atlas.top * invAtlasHeight
        val vBottom = if (atlasBottomOrigin) 1f - atlas.bottom * invAtlasHeight else atlas.bottom * invAtlasHeight

        emitVertex(builder, matrix, left, top, u0, vTop, r, g, b, a)
        emitVertex(builder, matrix, left, bottom, u0, vBottom, r, g, b, a)
        emitVertex(builder, matrix, right, bottom, u1, vBottom, r, g, b, a)
        emitVertex(builder, matrix, right, top, u1, vTop, r, g, b, a)
    }

    private fun emitVertex(
        builder: MeshBuilder,
        matrix: Matrix4f,
        x: Float, y: Float,
        u: Float, v: Float,
        r: Float, g: Float, b: Float, a: Float
    ) {
        builder.vertex(matrix, x, y, 0f)
            .element2f(UV0, u, v)
            .element4f(COLOR, r, g, b, a)
            .element3f(STYLE, 0f, 0.5f, 0f)
            .element4f(OUTLINE, 0f, 0f, 0f, 0f)
            .element4f(FADE, -1e5f, 1e5f, 0f, 0f)
            .element4f(SCISSOR, -1e5f, 0f, 0f, 0f)
    }

    data class Glyph(val advance: Float, val plane: PlaneBounds?, val atlas: AtlasBounds?)
    data class PlaneBounds(val left: Float, val bottom: Float, val right: Float, val top: Float)
    data class AtlasBounds(val left: Float, val bottom: Float, val right: Float, val top: Float)

    companion object {
        private val UV0 = MsdfTextRenderer.TextFormats.getVertexElement("UV0")
        private val COLOR = MsdfTextRenderer.TextFormats.getVertexElement("Color")
        private val STYLE = MsdfTextRenderer.TextFormats.getVertexElement("Style0")
        private val OUTLINE = MsdfTextRenderer.TextFormats.getVertexElement("OutlineColor0")
        private val FADE = MsdfTextRenderer.TextFormats.getVertexElement("Fade0")
        private val SCISSOR = MsdfTextRenderer.TextFormats.getVertexElement("Scissor0")
    }
}
