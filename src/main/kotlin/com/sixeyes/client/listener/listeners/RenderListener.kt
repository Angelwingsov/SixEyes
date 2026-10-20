package com.sixeyes.client.listener.listeners

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.renderpearl.backend.opengl.GlDevice
import com.mojang.renderpearl.backend.opengl.GlStateManager
import com.mojang.renderpearl.frontend.FrontendGpuDevice
import com.mojang.renderpearl.backend.opengl.GlBuffer
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.interfaces.IRenderable
import com.sixeyes.client.listener.Listener
import com.sixeyes.client.render.main.ChromaLoaders
import com.sixeyes.client.render.main.ChromaRenderer
import com.sixeyes.client.render.main.blend.DstFactor
import com.sixeyes.client.render.main.blend.SrcFactor
import com.sixeyes.client.render.main.compile.GlShaderLibrary
import com.sixeyes.client.render.main.compile.GlobalChromaCompiler
import com.sixeyes.client.util.render.font.Fonts
import com.sixeyes.client.util.render.font.MsdfTextRenderer
import com.sixeyes.client.util.render.rect.RectRenderer
import com.sixeyes.client.util.render.TextureLoader
import com.sixeyes.client.util.render.engine.Renderable
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.engine.controls.LayerControl
import com.sixeyes.client.util.render.engine.controls.MatrixControl
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.engine.dispatcher.RenderMetrics
import com.sixeyes.event.EventManager
import com.sixeyes.event.events.OverlayRenderEvent
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor
import java.util.Locale

object RenderListener : Listener() {
    var loaded = false
    private var shaderLibsRegistered = false
    private var chromaInitialized = false

    @JvmField
    val ALL_GUI_PIPELINES = arrayOf(
        ClientRenderPipeline.LOW, ClientRenderPipeline.MEDIUM, ClientRenderPipeline.HIGH,
        ClientRenderPipeline.HUD_RECT, ClientRenderPipeline.HUD_SPECIAL, ClientRenderPipeline.HUD_TEXT,
        ClientRenderPipeline.GUI_RECT, ClientRenderPipeline.GUI_SPECIAL, ClientRenderPipeline.GUI_TEXT,
        ClientRenderPipeline.WINDOW_RECT, ClientRenderPipeline.WINDOW_SPECIAL, ClientRenderPipeline.WINDOW_TEXT,
        ClientRenderPipeline.POPUP_RECT, ClientRenderPipeline.POPUP_SPECIAL, ClientRenderPipeline.POPUP_TEXT,
        ClientRenderPipeline.FIRST_RECT, ClientRenderPipeline.FIRST_SPECIAL, ClientRenderPipeline.FIRST_TEXT
    )

    private val cachedOverlayEvent = OverlayRenderEvent()

    override fun init() {
        if (isOpenGLActive()) {
            ensureChromaInitialized()
        }
        if (isOpenGLActive()) {
            ensureShaderLibsRegistered()
        }
    }

    fun hookRender(partialTicks: Float, vararg pipelines: ClientRenderPipeline) {
        hookRenderArray(partialTicks, pipelines)
    }

    fun renderVulkanHud(guiGraphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker) {
        if (isOpenGLActive()) return
        if (mc.player == null || mc.level == null) return

        RenderUtils.currentGraphics = guiGraphics
        try {
            val partialTicks = deltaTracker.getGameTimeDeltaPartialTick(false)
            val mouseX = InputListener.mouseX()
            val mouseY = InputListener.mouseY()

            val overlayRenderEvent = cachedOverlayEvent.apply {
                put(OverlayRenderEvent.PARTIAL_TICKS, partialTicks)
                put(OverlayRenderEvent.MOUSE_X, mouseX)
                put(OverlayRenderEvent.MOUSE_Y, mouseY)
            }

            EventManager.post(overlayRenderEvent)
            debugoverlay()
        } finally {
            RenderUtils.currentGraphics = null
        }
    }

    fun isOpenGLActive(): Boolean {
        val device = RenderSystem.tryGetDevice() ?: return false
        if (device is FrontendGpuDevice) {
            return device.backend is GlDevice
        }
        return false
    }

    fun hookRenderArray(partialTicks: Float, pipelines: Array<out ClientRenderPipeline>) {
        if (mc.player == null || mc.level == null) return
        if (!isOpenGLActive()) return




        init()

        if (!loaded) {
            LayerControl.loadShaders(MsdfTextRenderer, RectRenderer)
            LayerControl.loadRender()

            TextureLoader.load()
            Fonts.preload()

            loaded = true
            return
        }

        val mouseX = InputListener.mouseX()
        val mouseY = InputListener.mouseY()

        RenderMetrics.onFrameStart()

        MatrixControl.unscaledProjection()
        MatrixControl.reset()
        GlStateManager._disableDepthTest()

        ChromaRenderer.bindMainFramebuffer()
        ChromaRenderer.applyBlend(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ONE, DstFactor.ZERO)

        val overlayRenderEvent = cachedOverlayEvent.apply {
            put(OverlayRenderEvent.PARTIAL_TICKS, partialTicks)
            put(OverlayRenderEvent.MOUSE_X, mouseX)
            put(OverlayRenderEvent.MOUSE_Y, mouseY)
        }

        EventManager.post(overlayRenderEvent)

        val currentScreen = mc.gui.screen()
        if (currentScreen is IRenderable) {
            (currentScreen as IRenderable).render(mouseX, mouseY)
        }

        debugoverlay()

        LayerControl.flushPipelinesArray(pipelines)
        ChromaRenderer.endFrame()
        GlStateManager._enableDepthTest()
    }

    private fun debugoverlay() {
        val matrix = MatrixControl.matrix4fStack
        val screenW = mc.window.guiScaledWidth.toFloat()
        val screenH = mc.window.guiScaledHeight.toFloat()

        val btnY = 28f
        val btnSize = 9f
        val btnRadius = 2f
        val btnBg = Color4b(255, 255, 255, 240)
        val btnXs = floatArrayOf(6f, 21f, 36f)
        for (btnX in btnXs) { RenderUtils.roundedRect(matrix = matrix, x = btnX, y = btnY, width = btnSize, height = btnSize, color = btnBg, radius = btnRadius, pipeline = ClientRenderPipeline.HUD_RECT)
        Fonts.google_sans_regular.drawCenteredString(matrix = matrix, text = "Hi", centerX = btnX + btnSize * 0.5f, y = btnY + btnSize + 2f, size = 7f, color = Color4b(255, 255, 255, 240), shadow = false, pipeline = ClientRenderPipeline.HUD_TEXT) }

        val fps = mc.fps
        val frameMs = String.format(Locale.US, "%.2f", if (fps > 0) 1000f / fps else 0f)
        val draws = RenderMetrics.lastDrawCalls
        val verts = RenderMetrics.lastVertices
        val idx = RenderMetrics.lastIndices
        val batches = "${RenderMetrics.lastFlushes}/51"
        val metricsText = "fps: $fps | frame: ${frameMs}ms | drawcalls: $draws | verts: $verts | idx: $idx | batches: $batches"
        Fonts.google_sans_regular.drawRightString(matrix = matrix, text = metricsText, rightX = screenW - 6f, y = screenH - 12f, size = 8.5f, color = Color4b(230, 224, 233, 230), shadow = true, pipeline = ClientRenderPipeline.HUD_TEXT) }

    private fun registerShaderLibs() {
        GlobalChromaCompiler.registerShaderLibraries(
            registerShaderLib("shapes"),
            registerShaderLib("math_utils"),
            registerShaderLib("matrices"),
            registerShaderLib("scissor_check")
        )
    }

    private fun ensureShaderLibsRegistered() {
        if (shaderLibsRegistered) return
        registerShaderLibs()
        shaderLibsRegistered = true
    }

    private fun ensureChromaInitialized() {
        if (chromaInitialized) return
        if (ChromaRenderer.bufferIdGetter != null) {
            chromaInitialized = true
            return
        }

        ChromaRenderer.init(
            { glBuffer -> glBuffer.handle() },
            { mc.window.guiScale }
        )
        chromaInitialized = true
    }

    private fun registerShaderLib(file: String?): GlShaderLibrary {
        return ChromaLoaders.IN_JAR.createShaderLibraryBuilder()
            .name(file)
            .library(Renderable.SHADER_INCLUDE_PATH + file + ".glsl")
            .build()
    }
}