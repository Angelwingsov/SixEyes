package com.sixeyes.client.listener.listeners

import com.mojang.blaze3d.opengl.GlStateManager
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.interfaces.IRenderable
import com.sixeyes.client.listener.Listener
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
import com.sixeyes.event.EventManager
import com.sixeyes.event.events.OverlayRenderEvent

object RenderListener : Listener() {
    var loaded = false

    override fun init() {
        registerShaderLibs()
    }

    fun hookRender(partialTicks: Float, vararg pipelines: ClientRenderPipeline) {
        if (mc.player == null || mc.level == null) return

        if (!loaded) {
            LayerControl.loadShaders()
            LayerControl.loadRender()

            TextureLoader.load()

            loaded = true
            return
        }

        val mouseX = InputListener.mouseX()
        val mouseY = InputListener.mouseY()

        MatrixControl.unscaledProjection()
        MatrixControl.reset()
        GlStateManager._disableDepthTest()

        ChromaRenderer.bindMainFramebuffer()
        ChromaRenderer.applyBlend(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ONE, DstFactor.ZERO)

        val overlayRenderEvent = OverlayRenderEvent().apply {
            put(OverlayRenderEvent.PARTIAL_TICKS, partialTicks)
            put(OverlayRenderEvent.MOUSE_X, mouseX)
            put(OverlayRenderEvent.MOUSE_Y, mouseY)
        }

        EventManager.post(overlayRenderEvent)

        if (mc.screen is IRenderable) {
            (mc.screen as IRenderable).render(mouseX, mouseY)
        }

        LayerControl.flushPipelines(*pipelines)
        MatrixControl.scaledProjection()
    }

    private fun registerShaderLibs() {
        GlobalChromaCompiler.registerShaderLibraries(
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