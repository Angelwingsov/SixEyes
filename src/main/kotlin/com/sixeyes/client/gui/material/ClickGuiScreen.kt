package com.sixeyes.client.gui.material

import com.sixeyes.client.listener.listeners.RenderListener
import com.sixeyes.client.extensions.categories
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.gui.material.components.CategoryPanel
import com.sixeyes.client.interfaces.IRenderable
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.engine.controls.MatrixControl
import com.sixeyes.client.util.render.engine.dispatcher.RenderMetrics
import com.sixeyes.client.util.render.font.Fonts
import com.sixeyes.module.modules.render.ClickGuiModule
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import java.util.Locale

object ClickGuiScreen : Screen(Component.literal("SixEyes ClickGUI")), IRenderable {
    val panels: MutableList<CategoryPanel> = ArrayList()
    private var initialized = false

    private var lastFrameTime = System.currentTimeMillis()

    private fun initPanels() {
        if (initialized && panels.isNotEmpty()) return

        panels.clear()
        val panelW = 122f
        val gap = 8f
        val totalW = categories.size * panelW + (categories.size - 1) * gap
        val startX = ((mc.window.guiScaledWidth - totalW) * 0.5f).coerceAtLeast(10f)
        val startY = 178f.coerceAtLeast(20f)

        categories.forEachIndexed { i, category ->
            panels.add(CategoryPanel(category, startX + i * (panelW + gap), startY, panelW))
        }

        initialized = true
    }

    override fun init() {
        super.init()
        initPanels()
        lastFrameTime = System.currentTimeMillis()
    }

    override fun render(mouseX: Int, mouseY: Int) {
        val now = System.currentTimeMillis()
        val dt = ((now - lastFrameTime) / 1000f).coerceIn(0.0001f, 0.1f)
        lastFrameTime = now

        val matrix = MatrixControl.matrix4fStack
        val screenW = mc.window.guiScaledWidth.toFloat()
        val screenH = mc.window.guiScaledHeight.toFloat()

        if (ClickGuiModule.blur.value && mc.options.getMenuBackgroundBlurriness() == 0) {
            RenderUtils.rect(
                matrix = matrix,
                x = 0f,
                y = 0f,
                width = screenW,
                height = screenH,
                color = Color4b(0, 0, 0, 80),
                pipeline = ClientRenderPipeline.LOW
            )
        }

        for (panel in panels) {
            panel.render(matrix, mouseX, mouseY, dt)
        }
    }

    override fun extractRenderState(guiGraphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (!RenderListener.isOpenGLActive()) {
            RenderUtils.currentGraphics = guiGraphics
            try {
                render(mouseX, mouseY)
            } finally {
                RenderUtils.currentGraphics = null
            }
        }
    }

    override fun extractBackground(guiGraphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (mc.options.getMenuBackgroundBlurriness() > 0) {
            extractBlurredBackground(guiGraphics)
        }
    }

    private fun normalizeButton(button: Int): Int {
        return when (button) {
            InputConstants.MOUSE_BUTTON_LEFT -> 0
            InputConstants.MOUSE_BUTTON_RIGHT -> 1
            InputConstants.MOUSE_BUTTON_MIDDLE -> 2
            else -> button
        }
    }

    override fun mouseClicked(event: MouseButtonEvent, bl: Boolean): Boolean {
        val mouseX = event.x()
        val mouseY = event.y()
        val button = normalizeButton(event.button())

        for (i in panels.indices.reversed()) {
            val panel = panels[i]
            if (panel.mouseClicked(mouseX, mouseY, button)) {
                panels.removeAt(i)
                panels.add(panel)
                return true
            }
        }

        return super.mouseClicked(event, bl)
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        val mouseX = event.x()
        val mouseY = event.y()
        val button = normalizeButton(event.button())
        for (panel in panels) {
            panel.mouseReleased(mouseX, mouseY, button)
        }
        return super.mouseReleased(event)
    }

    override fun mouseDragged(event: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean {
        val mouseX = event.x()
        val mouseY = event.y()
        val button = normalizeButton(event.button())
        for (panel in panels) {
            panel.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        }
        return super.mouseDragged(event, deltaX, deltaY)
    }

    override fun keyPressed(event: KeyEvent): Boolean {
        val keyCode = event.key()
        val scanCode = event.keycode()
        val modifiers = event.modifiers()

        for (panel in panels) {
            if (panel.keyPressed(keyCode, scanCode, modifiers)) {
                return true
            }
        }

        if (keyCode == InputConstants.KEY_RSHIFT || keyCode == ClickGuiModule.key) {
            onClose()
            return true
        }

        return super.keyPressed(event)
    }

    override fun charTyped(event: CharacterEvent): Boolean {
        return super.charTyped(event)
    }

    override fun isPauseScreen(): Boolean = false

    override fun onClose() {
        super.onClose()
        ClickGuiModule.setEnabled(false)
    }
}
