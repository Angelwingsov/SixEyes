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
import com.sixeyes.client.util.render.ScissorUtil
import com.sixeyes.client.util.render.engine.Renderable
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.engine.controls.LayerControl
import org.joml.Matrix4f

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

    private val BASIC_BATCH_STATE = RectBatchState(RectType.BASIC, 0)

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

        val state = if (style.type == RectType.BASIC) BASIC_BATCH_STATE else RectBatchState(style.type, style.textureId)
        val builder = LayerControl.getBuilder(pipeline, this, state) ?: return

        val scissor = ScissorUtil.getCurrentScissorValues()
        val radiusLimit = (if (width < height) width else height) * 0.5f
        val radiusTl = style.corners.topLeft.coerceIn(0f, radiusLimit)
        val radiusTr = style.corners.topRight.coerceIn(0f, radiusLimit)
        val radiusBr = style.corners.bottomRight.coerceIn(0f, radiusLimit)
        val radiusBl = style.corners.bottomLeft.coerceIn(0f, radiusLimit)
        val mix = style.mix.coerceIn(0f, 1f)
        val alpha = style.alpha.coerceIn(0f, 1f)
        val mode = style.mode
        val borderWidth = style.border.width.coerceAtLeast(0f)
        val borderColor = style.border.color
        val typeId = style.type.id

        val colors = style.colors
        val trR = colors.topRight.r / 255f
        val trG = colors.topRight.g / 255f
        val trB = colors.topRight.b / 255f
        val trA = colors.topRight.a / 255f

        val tlR = colors.topLeft.r / 255f
        val tlG = colors.topLeft.g / 255f
        val tlB = colors.topLeft.b / 255f
        val tlA = colors.topLeft.a / 255f

        val brR = colors.bottomRight.r / 255f
        val brG = colors.bottomRight.g / 255f
        val brB = colors.bottomRight.b / 255f
        val brA = colors.bottomRight.a / 255f

        val blR = colors.bottomLeft.r / 255f
        val blG = colors.bottomLeft.g / 255f
        val blB = colors.bottomLeft.b / 255f
        val blA = colors.bottomLeft.a / 255f

        val bcR = borderColor.r / 255f
        val bcG = borderColor.g / 255f
        val bcB = borderColor.b / 255f
        val bcA = borderColor.a / 255f

        val uv = style.uv
        val u0 = uv.u0
        val v0 = uv.v0
        val u1 = uv.u1
        val v1 = uv.v1

        val sx = scissor.x
        val sy = scissor.y
        val sz = scissor.z
        val sw = scissor.w

        val x2 = x + width
        val y2 = y + height
        
        emitVertexDirect(builder, matrix, x, y, 0f, 0f, u0, v0, width, height,
            trR, trG, trB, trA, tlR, tlG, tlB, tlA, brR, brG, brB, brA, blR, blG, blB, blA,
            radiusTl, radiusTr, radiusBr, radiusBl, mix, alpha, mode, borderWidth, bcR, bcG, bcB, bcA, typeId, sx, sy, sz, sw)
        
        emitVertexDirect(builder, matrix, x, y2, 0f, 1f, u0, v1, width, height,
            trR, trG, trB, trA, tlR, tlG, tlB, tlA, brR, brG, brB, brA, blR, blG, blB, blA,
            radiusTl, radiusTr, radiusBr, radiusBl, mix, alpha, mode, borderWidth, bcR, bcG, bcB, bcA, typeId, sx, sy, sz, sw)
        
        emitVertexDirect(builder, matrix, x2, y2, 1f, 1f, u1, v1, width, height,
            trR, trG, trB, trA, tlR, tlG, tlB, tlA, brR, brG, brB, brA, blR, blG, blB, blA,
            radiusTl, radiusTr, radiusBr, radiusBl, mix, alpha, mode, borderWidth, bcR, bcG, bcB, bcA, typeId, sx, sy, sz, sw)
        
        emitVertexDirect(builder, matrix, x2, y, 1f, 0f, u1, v0, width, height,
            trR, trG, trB, trA, tlR, tlG, tlB, tlA, brR, brG, brB, brA, blR, blG, blB, blA,
            radiusTl, radiusTr, radiusBr, radiusBl, mix, alpha, mode, borderWidth, bcR, bcG, bcB, bcA, typeId, sx, sy, sz, sw)
    }

    private fun emitVertexDirect(
        builder: MeshBuilder,
        matrix: Matrix4f,
        x: Float, y: Float,
        localX: Float, localY: Float,
        u: Float, v: Float,
        width: Float, height: Float,
        trR: Float, trG: Float, trB: Float, trA: Float,
        tlR: Float, tlG: Float, tlB: Float, tlA: Float,
        brR: Float, brG: Float, brB: Float, brA: Float,
        blR: Float, blG: Float, blB: Float, blA: Float,
        radiusTl: Float, radiusTr: Float, radiusBr: Float, radiusBl: Float,
        mix: Float, alpha: Float, mode: Float, borderWidth: Float,
        bcR: Float, bcG: Float, bcB: Float, bcA: Float,
        type: Float,
        sx: Float, sy: Float, sz: Float, sw: Float
    ) {
        builder.vertex(matrix, x, y, 0f)
            .element4f(TopRightColor, trR, trG, trB, trA)
            .element4f(TopLeftColor, tlR, tlG, tlB, tlA)
            .element4f(BottomRightColor, brR, brG, brB, brA)
            .element4f(BottomLeftColor, blR, blG, blB, blA)
            .element2f(LocalCord, localX, localY)
            .element2f(uv0, u, v)
            .element2f(size0, width, height)
            .element4f(Radius0, radiusTl, radiusTr, radiusBr, radiusBl)
            .element1f(Mix0, mix)
            .element1f(Alpha0, alpha)
            .element1f(Mode0, mode)
            .element1f(BorderWidth, borderWidth)
            .element4f(BorderColor, bcR, bcG, bcB, bcA)
            .element1f(Type0, type)
            .element4f(Scissor0, sx, sy, sz, sw)
    }

    private val TopRightColor: VertexElement = RectFormat.getVertexElement("TopRightColor0")
    private val TopLeftColor: VertexElement = RectFormat.getVertexElement("TopLeftColor0")
    private val BottomRightColor: VertexElement = RectFormat.getVertexElement("BottomRightColor0")
    private val BottomLeftColor: VertexElement = RectFormat.getVertexElement("BottomLeftColor0")
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
