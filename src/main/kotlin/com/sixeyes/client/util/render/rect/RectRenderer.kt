package com.sixeyes.client.util.render.rect

import com.sixeyes.client.render.main.ChromaRenderer
import com.sixeyes.client.render.main.program.uniform.UniformType
import com.sixeyes.client.render.main.program.uniform.uniforms.sampler.SamplerUniform
import com.sixeyes.client.render.main.vertex.DrawMode
import com.sixeyes.client.render.main.vertex.element.VertexElement
import com.sixeyes.client.render.main.vertex.element.VertexElementType
import com.sixeyes.client.render.main.vertex.format.VertexFormat
import com.sixeyes.client.render.main.vertex.mesh.IMesh
import com.sixeyes.client.render.main.vertex.mesh.MeshBuilder
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.ScissorUtil
import com.sixeyes.client.util.render.engine.Renderable
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.engine.controls.LayerControl
import org.joml.Matrix4f
import org.joml.Vector4f

object RectRenderer : Renderable() {
    private data class RectBatchState(
        val type: RectType,
        val textureId: Int
    )

    val RectFormat: VertexFormat = VertexFormat.builder()
        .element("TopRightColor0", VertexElementType.FLOAT, 4)
        .element("TopLeftColor0", VertexElementType.FLOAT, 4)
        .element("BottomRightColor0", VertexElementType.FLOAT, 4)
        .element("BottomLeftColor0", VertexElementType.FLOAT, 4)
        .element("LocalCoord0", VertexElementType.FLOAT, 2)
        .element("UV0", VertexElementType.FLOAT, 2)
        .element("Size0", VertexElementType.FLOAT, 2)
        .element("Radius0", VertexElementType.FLOAT, 4)
        .element("Mix0", VertexElementType.FLOAT, 1)
        .element("Alpha0", VertexElementType.FLOAT, 1)
        .element("Mode0", VertexElementType.FLOAT, 1)
        .element("BorderWidth0", VertexElementType.FLOAT, 1)
        .element("BorderColor0", VertexElementType.FLOAT, 4)
        .element("Type0", VertexElementType.FLOAT, 1)
        .element("Scissor0", VertexElementType.FLOAT, 4)
        .build()

    private var textureUniform: SamplerUniform? = null

    override fun name(): String = "rect_renderer"

    override fun shader(): String = "rect/rectangle"

    override fun drawMode(): DrawMode = DrawMode.QUADS

    override fun vertexFormat(): VertexFormat = RectFormat

    override fun load() {
        glProgram = createShaderBuilder(name(), shader(), shader())
            .sampler("uTexture")
            .build()

        textureUniform = glProgram?.getUniform("uTexture", UniformType.SAMPLER)
    }

    override fun isBatchCompatible(oldState: Any?, newState: Any?): Boolean {
        val oldBatch = oldState as? RectBatchState ?: return false
        val newBatch = newState as? RectBatchState ?: return false
        return oldBatch == newBatch
    }

    override fun renderBatch(mesh: IMesh?, state: Any?) {
        val batch = state as? RectBatchState ?: return
        val program = glProgram ?: return

        setGlobalProgram(program)
        initMatrix()

        if (batch.type == RectType.TEXTURE) {
            textureUniform?.set(batch.textureId)
        }

        ChromaRenderer.draw(mesh)
    }

    fun emit(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        style: RectStyle,
        pipeline: ClientRenderPipeline
    ) {
        if (width <= 0f || height <= 0f) return
        if (style.type == RectType.TEXTURE && style.textureId <= 0) return

        val state = RectBatchState(style.type, if (style.type == RectType.TEXTURE) style.textureId else 0)
        val builder = LayerControl.getBuilder(pipeline, this, state) ?: return

        val scissor = ScissorUtil.getCurrentScissorValues()
        val normalized = normalizeStyle(style, width, height)
        val quad = buildQuad(x, y, width, height, normalized.uv)

        for (vertex in quad) {
            emitVertex(
                builder = builder,
                matrix = matrix,
                x = vertex.x,
                y = vertex.y,
                localX = vertex.localX,
                localY = vertex.localY,
                u = vertex.u,
                v = vertex.v,
                width = width,
                height = height,
                colors = normalized.colors,
                radiusTl = normalized.radiusTl,
                radiusTr = normalized.radiusTr,
                radiusBr = normalized.radiusBr,
                radiusBl = normalized.radiusBl,
                mix = normalized.mix,
                alpha = normalized.alpha,
                mode = normalized.mode,
                borderWidth = normalized.borderWidth,
                borderColor = normalized.borderColor,
                type = normalized.typeId,
                scissor = scissor
            )
        }
    }

    private fun buildQuad(x: Float, y: Float, width: Float, height: Float, uv: RectUv): Array<QuadVertex> {
        return arrayOf(
            QuadVertex(x, y, 0f, 0f, uv.u0, uv.v0),
            QuadVertex(x, y + height, 0f, 1f, uv.u0, uv.v1),
            QuadVertex(x + width, y + height, 1f, 1f, uv.u1, uv.v1),
            QuadVertex(x + width, y, 1f, 0f, uv.u1, uv.v0)
        )
    }

    private fun normalizeStyle(style: RectStyle, width: Float, height: Float): NormalizedRectStyle {
        val radiusLimit = (kotlin.math.min(width, height) * 0.5f).coerceAtLeast(0f)
        return NormalizedRectStyle(
            colors = style.colors,
            uv = style.uv,
            radiusTl = style.corners.topLeft.coerceIn(0f, radiusLimit),
            radiusTr = style.corners.topRight.coerceIn(0f, radiusLimit),
            radiusBr = style.corners.bottomRight.coerceIn(0f, radiusLimit),
            radiusBl = style.corners.bottomLeft.coerceIn(0f, radiusLimit),
            mix = style.mix.coerceIn(0f, 1f),
            alpha = style.alpha.coerceIn(0f, 1f),
            mode = style.mode,
            borderWidth = style.border.width.coerceAtLeast(0f),
            borderColor = style.border.color,
            typeId = style.type.id
        )
    }

    private data class NormalizedRectStyle(
        val colors: RectColors,
        val uv: RectUv,
        val radiusTl: Float,
        val radiusTr: Float,
        val radiusBr: Float,
        val radiusBl: Float,
        val mix: Float,
        val alpha: Float,
        val mode: Float,
        val borderWidth: Float,
        val borderColor: Color4b,
        val typeId: Float
    )

    private data class QuadVertex(
        val x: Float,
        val y: Float,
        val localX: Float,
        val localY: Float,
        val u: Float,
        val v: Float
    )

    private fun emitVertex(
        builder: MeshBuilder,
        matrix: Matrix4f,
        x: Float,
        y: Float,
        localX: Float,
        localY: Float,
        u: Float,
        v: Float,
        width: Float,
        height: Float,
        colors: RectColors,
        radiusTl: Float,
        radiusTr: Float,
        radiusBr: Float,
        radiusBl: Float,
        mix: Float,
        alpha: Float,
        mode: Float,
        borderWidth: Float,
        borderColor: Color4b,
        type: Float,
        scissor: Vector4f
    ) {
        builder.vertex(matrix, x, y, 0f)
            .element4f(TOP_RIGHT_COLOR, colors.topRight.rf(), colors.topRight.gf(), colors.topRight.bf(), colors.topRight.af())
            .element4f(TOP_LEFT_COLOR, colors.topLeft.rf(), colors.topLeft.gf(), colors.topLeft.bf(), colors.topLeft.af())
            .element4f(BOTTOM_RIGHT_COLOR, colors.bottomRight.rf(), colors.bottomRight.gf(), colors.bottomRight.bf(), colors.bottomRight.af())
            .element4f(BOTTOM_LEFT_COLOR, colors.bottomLeft.rf(), colors.bottomLeft.gf(), colors.bottomLeft.bf(), colors.bottomLeft.af())
            .element2f(LocalCord, localX, localY)
            .element2f(uv0, u, v)
            .element2f(size0, width, height)
            .element4f(Radius0, radiusTl, radiusTr, radiusBr, radiusBl)
            .element(Mix0, VertexElementType.FLOAT, mix)
            .element(Alpha0, VertexElementType.FLOAT, alpha)
            .element(Mode0, VertexElementType.FLOAT, mode)
            .element(BorderWidth, VertexElementType.FLOAT, borderWidth)
            .element4f(BorderColor, borderColor.rf(), borderColor.gf(), borderColor.bf(), borderColor.af())
            .element(Type0, VertexElementType.FLOAT, type)
            .element4f(Scissor0, scissor.x, scissor.y, scissor.z, scissor.w)
    }

    private fun Color4b.rf(): Float = r / 255f
    private fun Color4b.gf(): Float = g / 255f
    private fun Color4b.bf(): Float = b / 255f
    private fun Color4b.af(): Float = a / 255f

    private val TOP_RIGHT_COLOR: VertexElement = RectFormat.getVertexElement("TopRightColor0")
    private val TOP_LEFT_COLOR: VertexElement = RectFormat.getVertexElement("TopLeftColor0")
    private val BOTTOM_RIGHT_COLOR: VertexElement = RectFormat.getVertexElement("BottomRightColor0")
    private val BOTTOM_LEFT_COLOR: VertexElement = RectFormat.getVertexElement("BottomLeftColor0")
    private val LocalCord: VertexElement = RectFormat.getVertexElement("LocalCoord0")
    private val uv0: VertexElement = RectFormat.getVertexElement("UV0")
    private val size0: VertexElement = RectFormat.getVertexElement("Size0")
    private val Radius0: VertexElement = RectFormat.getVertexElement("Radius0")
    private val Mix0: VertexElement = RectFormat.getVertexElement("Mix0")
    private val Alpha0: VertexElement = RectFormat.getVertexElement("Alpha0")
    private val Mode0: VertexElement = RectFormat.getVertexElement("Mode0")
    private val BorderWidth: VertexElement = RectFormat.getVertexElement("BorderWidth0")
    private val BorderColor: VertexElement = RectFormat.getVertexElement("BorderColor0")
    private val Type0: VertexElement = RectFormat.getVertexElement("Type0")
    private val Scissor0: VertexElement = RectFormat.getVertexElement("Scissor0")
}
