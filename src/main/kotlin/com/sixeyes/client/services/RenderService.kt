package com.sixeyes.client.services

import com.mojang.blaze3d.opengl.GlStateManager
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.render.main.ChromaLoaders
import com.sixeyes.client.render.main.ChromaRenderer
import com.sixeyes.client.render.main.blend.DstFactor
import com.sixeyes.client.render.main.blend.SrcFactor
import com.sixeyes.client.render.main.compile.GlShaderLibrary
import com.sixeyes.client.render.main.compile.GlobalChromaCompiler
import com.sixeyes.client.util.render.TextureLoader
import com.sixeyes.client.util.render.engine.Renderable
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.engine.controls.LayerControl
import com.sixeyes.client.util.render.engine.controls.MatrixControl

object RenderService {
    var init = false

    fun hookRender(partialTicks: Float, vararg pipelines: ClientRenderPipeline) {
        if (mc.player == null || mc.level == null) return

        if (!init) {
            LayerControl.loadShaders()
            LayerControl.loadRender()

            TextureLoader.load()

            init = true
            return
        }

        MatrixControl.unscaledProjection()
        MatrixControl.reset()
        GlStateManager._disableDepthTest()

        ChromaRenderer.bindMainFramebuffer()
        ChromaRenderer.applyBlend(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ONE, DstFactor.ZERO)

        // post render2dEvent

        //if (mc.screen is ScreenClickGui) {
        //    // render
        //}

        LayerControl.flushPipelines(*pipelines)
        MatrixControl.scaledProjection()
    }

    private fun registerShaderLibs() {
        GlobalChromaCompiler.registerShaderLibraries(
            registerShaderLib("math_utils"),
            registerShaderLib("shapes"),
            registerShaderLib("matrices"),
            registerShaderLib("scissor_check")
        )
    }

    private fun registerShaderLib(file: String?): GlShaderLibrary {
        return ChromaLoaders.IN_JAR.createShaderLibraryBuilder()
            .name(file)
            .library(Renderable.SHADER_INCLUDE_PATH + file + ".glsl")
            .build()
    }
}