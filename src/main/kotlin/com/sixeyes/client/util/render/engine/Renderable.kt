package com.sixeyes.client.util.render.engine

import com.sixeyes.client.extensions.CLIENT_ID
import com.sixeyes.client.render.main.ChromaLoaders
import com.sixeyes.client.render.main.ChromaRenderer
import com.sixeyes.client.render.main.builders.GlProgramBuilder
import com.sixeyes.client.render.main.program.GlProgram
import com.sixeyes.client.render.main.program.shader.ShaderType
import com.sixeyes.client.render.main.vertex.DrawMode
import com.sixeyes.client.render.main.vertex.format.VertexFormat
import com.sixeyes.client.render.main.vertex.mesh.IMesh

abstract class Renderable {

    companion object {
        val SHADER_PATH = "assets/${CLIENT_ID}/shaders/"
        val SHADER_CORE_PATH = "${SHADER_PATH}core/"
        val SHADER_INCLUDE_PATH = "${SHADER_PATH}include/"
    }

    abstract fun name(): String
    abstract fun shader(): String
    abstract fun drawMode(): DrawMode?
    abstract fun vertexFormat(): VertexFormat?

    abstract fun load()

    var glProgram: GlProgram? = null

    open fun isBatchCompatible(oldState: Any?, newState: Any?): Boolean {
        return oldState == newState
    }

    open fun renderBatch(mesh: IMesh?, state: Any?) {
        setGlobalProgram(glProgram)
        initMatrix()
        ChromaRenderer.draw(mesh)
    }

    fun createShaderBuilder(
        name: String?,
        fsh: String?,
        vsh: String?
    ): GlProgramBuilder<*> {
        return ChromaLoaders.IN_JAR.createProgramBuilder(ChromaRenderer.matrixSnippet)
            .name(name)
            .shader(
                ChromaLoaders.IN_JAR.createGlslFileEntry("$name-vertex",  "$SHADER_CORE_PATH$vsh.vsh"),
                ShaderType.Vertex
            )
            .shader(
                ChromaLoaders.IN_JAR.createGlslFileEntry("$name-fragment", "$SHADER_CORE_PATH$fsh.fsh"),
                ShaderType.Fragment
            )
    }

    fun initMatrix() {
        ChromaRenderer.initMatrix()
    }

    fun setGlobalProgram(globalProgram: GlProgram?) {
        ChromaRenderer.setGlobalProgram(globalProgram)
    }
}