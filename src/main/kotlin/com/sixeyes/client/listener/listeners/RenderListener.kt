package com.sixeyes.client.listener.listeners

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.renderpearl.backend.opengl.GlDevice
import com.mojang.renderpearl.backend.opengl.GlStateManager
import com.mojang.renderpearl.frontend.FrontendGpuDevice
import com.sixeyes.client.extensions.inGame
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.interfaces.IRenderable
import com.sixeyes.client.listener.Listener
import com.sixeyes.client.render.main.ChromaLoaders
import com.sixeyes.client.render.main.ChromaRenderer
import com.sixeyes.client.render.main.blend.DstFactor
import com.sixeyes.client.render.main.blend.SrcFactor
import com.sixeyes.client.render.main.compile.GlShaderLibrary
import com.sixeyes.client.render.main.compile.GlobalChromaCompiler
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.TextureLoader
import com.sixeyes.client.util.render.engine.Renderable
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.engine.controls.LayerControl
import com.sixeyes.client.util.render.engine.controls.MatrixControl
import com.sixeyes.client.util.render.engine.dispatcher.RenderMetrics
import com.sixeyes.client.util.render.font.Fonts
import com.sixeyes.client.util.render.font.MsdfTextRenderer
import com.sixeyes.client.util.render.rect.RectRenderer
import com.sixeyes.event.EventManager
import com.sixeyes.event.events.OverlayRenderEvent
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor

object RenderListener : Listener() {
    var loaded = false
        private set

    private var shaderLibsRegistered = false
    private var chromaInitialized = false
    private var cachedOpenGLActive: Boolean? = null

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
            ensureShaderLibsRegistered()
        }
    }

    fun hookRender(partialTicks: Float, vararg pipelines: ClientRenderPipeline) {
        hookRenderArray(partialTicks, pipelines)
    }

    fun renderVulkanHud(guiGraphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker) {
        if (isOpenGLActive() || !inGame) return

        RenderUtils.currentGraphics = guiGraphics
        try {
            val partialTicks = deltaTracker.getGameTimeDeltaPartialTick(false)
            val mouseX = InputListener.mouseX()
            val mouseY = InputListener.mouseY()

            EventManager.post(cachedOverlayEvent.update(partialTicks, mouseX, mouseY))
            if (mc.gui.screen() == null) {
                renderMetricsOverlay()
            }
        } finally {
            RenderUtils.currentGraphics = null
        }
    }

    fun renderVulkanScreenOverlay(guiGraphics: GuiGraphicsExtractor) {
        if (isOpenGLActive()) return

        RenderUtils.currentGraphics = guiGraphics
        try {
            renderMetricsOverlay()
        } finally {
            RenderUtils.currentGraphics = null
        }
    }

    fun isOpenGLActive(): Boolean {
        cachedOpenGLActive?.let { return it }

        val device = RenderSystem.tryGetDevice() ?: return false
        val detected = when {
            device is FrontendGpuDevice -> device.backend is GlDevice
            device is GlDevice -> true
            else -> {
                val backendName = runCatching {
                    val info = device.javaClass.getMethod("getDeviceInfo").invoke(device)
                    info?.javaClass?.getMethod("backendName")?.invoke(info)?.toString()
                }.getOrNull()
                backendName?.contains("OpenGL", ignoreCase = true)
                    ?: device.javaClass.simpleName.contains("Gl", ignoreCase = true)
            }
        }
        cachedOpenGLActive = detected
        return detected
    }

    fun hookRenderArray(partialTicks: Float, pipelines: Array<out ClientRenderPipeline>) {
        if (!isOpenGLActive()) return
        if (!inGame && mc.gui.screen() == null) return

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

        if (inGame) {
            EventManager.post(cachedOverlayEvent.update(partialTicks, mouseX, mouseY))
        }

        val currentScreen = mc.gui.screen()
        if (currentScreen is IRenderable) {
            currentScreen.render(mouseX, mouseY)
        }

        renderMetricsOverlay()

        LayerControl.flushPipelinesArray(pipelines)
        ChromaRenderer.endFrame()
        GlStateManager._enableDepthTest()
    }

    private fun renderMetricsOverlay() {
        val matrix = MatrixControl.matrix4fStack
        val screenW = mc.window.guiScaledWidth.toFloat()
        val screenH = mc.window.guiScaledHeight.toFloat()

        val fps = mc.fps
        val ms = if (fps > 0) (100000 / fps) else 0
        val msInt = ms / 100
        val msFrac = ms % 100
        val frameMs = if (msFrac < 10) "$msInt.0$msFrac" else "$msInt.$msFrac"
        val draws = RenderMetrics.lastDrawCalls
        val verts = RenderMetrics.lastVertices
        val idx = RenderMetrics.lastIndices
        val batches = "${RenderMetrics.lastFlushes}/51"
        val metricsText = "fps: $fps | frame: ${frameMs}ms | drawcalls: $draws | verts: $verts | idx: $idx | batches: $batches"

        Fonts.google_sans_regular.drawRightString(
            matrix = matrix,
            text = metricsText,
            rightX = screenW - 6f,
            y = screenH - 12f,
            size = 8.5f,
            color = Color4b(230, 224, 233, 230),
            shadow = true,
            pipeline = ClientRenderPipeline.HUD_TEXT
        )
    }

    private fun ensureShaderLibsRegistered() {
        if (shaderLibsRegistered) return
        GlobalChromaCompiler.registerShaderLibraries(
            registerShaderLib("shapes"),
            registerShaderLib("math_utils"),
            registerShaderLib("matrices"),
            registerShaderLib("scissor_check")
        )
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

    private fun registerShaderLib(file: String): GlShaderLibrary {
        return ChromaLoaders.IN_JAR.createShaderLibraryBuilder()
            .name(file)
            .library("${Renderable.SHADER_INCLUDE_PATH}$file.glsl")
            .build()
    }
}
