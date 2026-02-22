package com.sixeyes.client.util.render.font

import com.sixeyes.client.render.main.ChromaLoaders
import com.sixeyes.client.render.main.ChromaRenderer
import com.sixeyes.client.render.main.program.shader.ShaderType
import com.sixeyes.client.render.main.program.uniform.UniformType
import com.sixeyes.client.render.main.program.uniform.uniforms.FloatUniform
import com.sixeyes.client.render.main.program.uniform.uniforms.sampler.SamplerUniform
import com.sixeyes.client.render.main.vertex.DrawMode
import com.sixeyes.client.render.main.vertex.element.VertexElementType
import com.sixeyes.client.render.main.vertex.format.VertexFormat
import com.sixeyes.client.render.main.vertex.mesh.IMesh
import com.sixeyes.client.render.texture.texture.GLTexture
import com.sixeyes.client.util.render.engine.Renderable

internal data class MsdfBatchState(
    val texture: GLTexture,
    val range: Float
)

object MsdfTextRenderer : Renderable() {
    val TextFormats: VertexFormat = VertexFormat.builder()
        .element("UV0", VertexElementType.FLOAT, 2)
        .element("Color", VertexElementType.FLOAT, 4)
        .element("Style0", VertexElementType.FLOAT, 3)
        .element("OutlineColor0", VertexElementType.FLOAT, 4)
        .element("Fade0", VertexElementType.FLOAT, 4)
        .element("Scissor0", VertexElementType.FLOAT, 4)
        .build()

    private var rangeUniform: FloatUniform? = null
    private var textureUniform: SamplerUniform? = null

    override fun name(): String = "msdf_text"

    override fun shader(): String = "font/text"

    override fun drawMode(): DrawMode = DrawMode.QUADS

    override fun vertexFormat(): VertexFormat = TextFormats

    override fun load() {
        glProgram = ChromaLoaders.IN_JAR.createProgramBuilder(ChromaRenderer.matrixSnippet)
            .name(name())
            .shader(
                ChromaLoaders.IN_JAR.createGlslFileEntry(
                    "text-vertex",
                    "${SHADER_CORE_PATH}font/text.vsh"
                ),
                ShaderType.Vertex
            )
            .shader(
                ChromaLoaders.IN_JAR.createGlslFileEntry(
                    "text-fragment",
                    "${SHADER_CORE_PATH}font/text.fsh"
                ),
                ShaderType.Fragment
            )
            .sampler("uTexture")
            .uniform("uRange", UniformType.FLOAT)
            .build()

        val program = glProgram ?: return
        rangeUniform = program.getUniform("uRange", UniformType.FLOAT)
        textureUniform = program.getUniform("uTexture", UniformType.SAMPLER)
    }

    override fun isBatchCompatible(oldState: Any?, newState: Any?): Boolean {
        val oldBatch = oldState as? MsdfBatchState ?: return false
        val newBatch = newState as? MsdfBatchState ?: return false

        return oldBatch.texture.texId == newBatch.texture.texId &&
                oldBatch.range.toBits() == newBatch.range.toBits()
    }

    override fun renderBatch(mesh: IMesh?, state: Any?) {
        val batch = state as? MsdfBatchState ?: return
        val program = glProgram ?: return
        setGlobalProgram(program)
        initMatrix()
        rangeUniform?.set(batch.range)
        textureUniform?.set(batch.texture)

        ChromaRenderer.draw(mesh)
    }
}
