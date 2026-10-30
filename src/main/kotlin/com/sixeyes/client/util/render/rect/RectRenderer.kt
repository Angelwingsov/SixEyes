package com.sixeyes.client.util.render.rect

import com.sixeyes.client.render.main.ChromaRenderer
import com.sixeyes.client.render.main.program.uniform.UniformType
import com.sixeyes.client.render.main.program.uniform.uniforms.sampler.SamplerUniform
import com.sixeyes.client.render.main.vertex.DrawMode
import com.sixeyes.client.render.main.vertex.element.VertexElementType
import com.sixeyes.client.render.main.vertex.format.VertexFormat
import com.sixeyes.client.render.main.vertex.mesh.IMesh
import com.sixeyes.client.render.main.vertex.mesh.MeshBuilder
import com.sixeyes.client.util.render.ScissorUtil
import com.sixeyes.client.util.render.engine.Renderable
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.engine.controls.LayerControl
import org.joml.Matrix4f
import org.lwjgl.system.MemoryUtil

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

    fun emitDirect(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        trR: Float, trG: Float, trB: Float, trA: Float,
        tlR: Float, tlG: Float, tlB: Float, tlA: Float,
        brR: Float, brG: Float, brB: Float, brA: Float,
        blR: Float, blG: Float, blB: Float, blA: Float,
        radiusTl: Float, radiusTr: Float, radiusBr: Float, radiusBl: Float,
        mix: Float = 0f,
        alpha: Float = 1f,
        mode: Float = 0f,
        borderWidth: Float = 0f,
        bcR: Float = 0f, bcG: Float = 0f, bcB: Float = 0f, bcA: Float = 0f,
        typeId: Float = 0f,
        textureId: Int = 0,
        u0: Float = 0f, v0: Float = 0f, u1: Float = 1f, v1: Float = 1f,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        if (width <= 0f || height <= 0f) return
        val isTex = typeId == 1f
        if (isTex && textureId <= 0) return

        val state = if (!isTex) BASIC_BATCH_STATE else RectBatchState(RectType.TEXTURE, textureId)
        val builder = LayerControl.getBuilder(pipeline, this, state) ?: return

        val scissor = ScissorUtil.getCurrentScissorValues()
        val radiusLimit = (if (width < height) width else height) * 0.5f
        val rTl = radiusTl.coerceIn(0f, radiusLimit)
        val rTr = radiusTr.coerceIn(0f, radiusLimit)
        val rBr = radiusBr.coerceIn(0f, radiusLimit)
        val rBl = radiusBl.coerceIn(0f, radiusLimit)
        val bw = borderWidth.coerceAtLeast(0f)

        val pad = 1.5f
        val px1 = x - pad
        val py1 = y - pad
        val px2 = x + width + pad
        val py2 = y + height + pad

        val lx1 = -pad
        val ly1 = -pad
        val lx2 = width + pad
        val ly2 = height + pad

        val uPad = if (width > 0f) (u1 - u0) * (pad / width) else 0f
        val vPad = if (height > 0f) (v1 - v0) * (pad / height) else 0f
        val pu0 = u0 - uPad
        val pv0 = v0 - vPad
        val pu1 = u1 + uPad
        val pv1 = v1 + vPad

        val sx = scissor.x
        val sy = scissor.y
        val sz = scissor.z
        val sw = scissor.w

        val m00 = matrix.m00()
        val m01 = matrix.m01()
        val m10 = matrix.m10()
        val m11 = matrix.m11()
        val m30 = matrix.m30()
        val m31 = matrix.m31()

        val ptr = builder.reserveQuad()
        if (ptr == -1L) return

        writeQuadDirect(
            ptr,
            m00, m01, m10, m11, m30, m31,
            px1, py1, px2, py2,
            lx1, ly1, lx2, ly2,
            pu0, pv0, pu1, pv1,
            width, height,
            trR, trG, trB, trA,
            tlR, tlG, tlB, tlA,
            brR, brG, brB, brA,
            blR, blG, blB, blA,
            rTl, rTr, rBr, rBl,
            mix, alpha, mode, bw,
            bcR, bcG, bcB, bcA,
            typeId, sx, sy, sz, sw
        )
    }

    private fun writeQuadDirect(
        ptr: Long,
        m00: Float, m01: Float, m10: Float, m11: Float, m30: Float, m31: Float,
        px1: Float, py1: Float, px2: Float, py2: Float,
        lx1: Float, ly1: Float, lx2: Float, ly2: Float,
        pu0: Float, pv0: Float, pu1: Float, pv1: Float,
        width: Float, height: Float,
        trR: Float, trG: Float, trB: Float, trA: Float,
        tlR: Float, tlG: Float, tlB: Float, tlA: Float,
        brR: Float, brG: Float, brB: Float, brA: Float,
        blR: Float, blG: Float, blB: Float, blA: Float,
        rTl: Float, rTr: Float, rBr: Float, rBl: Float,
        mix: Float, alpha: Float, mode: Float, borderWidth: Float,
        bcR: Float, bcG: Float, bcB: Float, bcA: Float,
        typeId: Float,
        sx: Float, sy: Float, sz: Float, sw: Float
    ) {
        val v0 = ptr
        MemoryUtil.memPutFloat(v0, m00 * px1 + m10 * py1 + m30)
        MemoryUtil.memPutFloat(v0 + 4L, m01 * px1 + m11 * py1 + m31)
        MemoryUtil.memPutFloat(v0 + 8L, 0f)
        writeCommonAndCoords(v0, lx1, ly1, pu0, pv0, width, height,
            trR, trG, trB, trA, tlR, tlG, tlB, tlA, brR, brG, brB, brA, blR, blG, blB, blA,
            rTl, rTr, rBr, rBl, mix, alpha, mode, borderWidth, bcR, bcG, bcB, bcA, typeId, sx, sy, sz, sw)

        val v1 = ptr + 168L
        MemoryUtil.memPutFloat(v1, m00 * px1 + m10 * py2 + m30)
        MemoryUtil.memPutFloat(v1 + 4L, m01 * px1 + m11 * py2 + m31)
        MemoryUtil.memPutFloat(v1 + 8L, 0f)
        writeCommonAndCoords(v1, lx1, ly2, pu0, pv1, width, height,
            trR, trG, trB, trA, tlR, tlG, tlB, tlA, brR, brG, brB, brA, blR, blG, blB, blA,
            rTl, rTr, rBr, rBl, mix, alpha, mode, borderWidth, bcR, bcG, bcB, bcA, typeId, sx, sy, sz, sw)

        val v2 = ptr + 336L
        MemoryUtil.memPutFloat(v2, m00 * px2 + m10 * py2 + m30)
        MemoryUtil.memPutFloat(v2 + 4L, m01 * px2 + m11 * py2 + m31)
        MemoryUtil.memPutFloat(v2 + 8L, 0f)
        writeCommonAndCoords(v2, lx2, ly2, pu1, pv1, width, height,
            trR, trG, trB, trA, tlR, tlG, tlB, tlA, brR, brG, brB, brA, blR, blG, blB, blA,
            rTl, rTr, rBr, rBl, mix, alpha, mode, borderWidth, bcR, bcG, bcB, bcA, typeId, sx, sy, sz, sw)

        val v3 = ptr + 504L
        MemoryUtil.memPutFloat(v3, m00 * px2 + m10 * py1 + m30)
        MemoryUtil.memPutFloat(v3 + 4L, m01 * px2 + m11 * py1 + m31)
        MemoryUtil.memPutFloat(v3 + 8L, 0f)
        writeCommonAndCoords(v3, lx2, ly1, pu1, pv0, width, height,
            trR, trG, trB, trA, tlR, tlG, tlB, tlA, brR, brG, brB, brA, blR, blG, blB, blA,
            rTl, rTr, rBr, rBl, mix, alpha, mode, borderWidth, bcR, bcG, bcB, bcA, typeId, sx, sy, sz, sw)
    }

    private inline fun writeCommonAndCoords(
        v: Long,
        localX: Float, localY: Float,
        u: Float, vCoord: Float,
        width: Float, height: Float,
        trR: Float, trG: Float, trB: Float, trA: Float,
        tlR: Float, tlG: Float, tlB: Float, tlA: Float,
        brR: Float, brG: Float, brB: Float, brA: Float,
        blR: Float, blG: Float, blB: Float, blA: Float,
        rTl: Float, rTr: Float, rBr: Float, rBl: Float,
        mix: Float, alpha: Float, mode: Float, borderWidth: Float,
        bcR: Float, bcG: Float, bcB: Float, bcA: Float,
        typeId: Float,
        sx: Float, sy: Float, sz: Float, sw: Float
    ) {
        MemoryUtil.memPutFloat(v + 12L, trR)
        MemoryUtil.memPutFloat(v + 16L, trG)
        MemoryUtil.memPutFloat(v + 20L, trB)
        MemoryUtil.memPutFloat(v + 24L, trA)

        MemoryUtil.memPutFloat(v + 28L, tlR)
        MemoryUtil.memPutFloat(v + 32L, tlG)
        MemoryUtil.memPutFloat(v + 36L, tlB)
        MemoryUtil.memPutFloat(v + 40L, tlA)

        MemoryUtil.memPutFloat(v + 44L, brR)
        MemoryUtil.memPutFloat(v + 48L, brG)
        MemoryUtil.memPutFloat(v + 52L, brB)
        MemoryUtil.memPutFloat(v + 56L, brA)

        MemoryUtil.memPutFloat(v + 60L, blR)
        MemoryUtil.memPutFloat(v + 64L, blG)
        MemoryUtil.memPutFloat(v + 68L, blB)
        MemoryUtil.memPutFloat(v + 72L, blA)

        MemoryUtil.memPutFloat(v + 76L, localX)
        MemoryUtil.memPutFloat(v + 80L, localY)

        MemoryUtil.memPutFloat(v + 84L, u)
        MemoryUtil.memPutFloat(v + 88L, vCoord)

        MemoryUtil.memPutFloat(v + 92L, width)
        MemoryUtil.memPutFloat(v + 96L, height)

        MemoryUtil.memPutFloat(v + 100L, rTl)
        MemoryUtil.memPutFloat(v + 104L, rTr)
        MemoryUtil.memPutFloat(v + 108L, rBr)
        MemoryUtil.memPutFloat(v + 112L, rBl)

        MemoryUtil.memPutFloat(v + 116L, mix)
        MemoryUtil.memPutFloat(v + 120L, alpha)
        MemoryUtil.memPutFloat(v + 124L, mode)
        MemoryUtil.memPutFloat(v + 128L, borderWidth)

        MemoryUtil.memPutFloat(v + 132L, bcR)
        MemoryUtil.memPutFloat(v + 136L, bcG)
        MemoryUtil.memPutFloat(v + 140L, bcB)
        MemoryUtil.memPutFloat(v + 144L, bcA)

        MemoryUtil.memPutFloat(v + 148L, typeId)

        MemoryUtil.memPutFloat(v + 152L, sx)
        MemoryUtil.memPutFloat(v + 156L, sy)
        MemoryUtil.memPutFloat(v + 160L, sz)
        MemoryUtil.memPutFloat(v + 164L, sw)
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
        val colors = style.colors
        val corners = style.corners
        val border = style.border
        val uv = style.uv

        emitDirect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            trR = colors.topRight.r / 255f,
            trG = colors.topRight.g / 255f,
            trB = colors.topRight.b / 255f,
            trA = colors.topRight.a / 255f,
            tlR = colors.topLeft.r / 255f,
            tlG = colors.topLeft.g / 255f,
            tlB = colors.topLeft.b / 255f,
            tlA = colors.topLeft.a / 255f,
            brR = colors.bottomRight.r / 255f,
            brG = colors.bottomRight.g / 255f,
            brB = colors.bottomRight.b / 255f,
            brA = colors.bottomRight.a / 255f,
            blR = colors.bottomLeft.r / 255f,
            blG = colors.bottomLeft.g / 255f,
            blB = colors.bottomLeft.b / 255f,
            blA = colors.bottomLeft.a / 255f,
            radiusTl = corners.topLeft,
            radiusTr = corners.topRight,
            radiusBr = corners.bottomRight,
            radiusBl = corners.bottomLeft,
            mix = style.mix.coerceIn(0f, 1f),
            alpha = style.alpha.coerceIn(0f, 1f),
            mode = style.mode,
            borderWidth = border.width,
            bcR = border.color.r / 255f,
            bcG = border.color.g / 255f,
            bcB = border.color.b / 255f,
            bcA = border.color.a / 255f,
            typeId = style.type.id,
            textureId = style.textureId,
            u0 = uv.u0,
            v0 = uv.v0,
            u1 = uv.u1,
            v1 = uv.v1,
            pipeline = pipeline
        )
    }
}
