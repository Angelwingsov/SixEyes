package com.sixeyes.client.gui.material

import com.mojang.blaze3d.platform.InputConstants
import com.sixeyes.client.extensions.categories
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.gui.material.components.CategoryPanel
import com.sixeyes.client.gui.material.components.ColorPickerWindow
import com.sixeyes.client.gui.material.components.KeybindComponent
import com.sixeyes.client.gui.material.components.ModuleComponent
import com.sixeyes.client.interfaces.IRenderable
import com.sixeyes.client.listener.listeners.RenderListener
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.engine.controls.MatrixControl
import com.sixeyes.module.modules.render.ClickGuiModule
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

object ClickGuiScreen : Screen(Component.literal("SixEyes ClickGUI")), IRenderable {
    val panels: MutableList<CategoryPanel> = ArrayList()
    private var initialized = false

    var parentScreen: Screen? = null
    var activeColorPicker: ColorPickerWindow? = null

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

        activeColorPicker?.render(matrix, mouseX, mouseY, dt)
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
        if (mc.level == null) {
            extractPanorama(guiGraphics, partialTick)
        }
        if (mc.options.getMenuBackgroundBlurriness() > 0) {
            extractBlurredBackground(guiGraphics)
        }
    }

    private fun normalizeButton(button: Int): Int = when (button) {
        InputConstants.MOUSE_BUTTON_LEFT -> 0
        InputConstants.MOUSE_BUTTON_RIGHT -> 1
        InputConstants.MOUSE_BUTTON_MIDDLE -> 2
        else -> button
    }

    fun clearAllListening(exceptModule: ModuleComponent? = null, exceptKeybind: KeybindComponent? = null) {
        for (panel in panels) {
            for (modComp in panel.moduleComponents) {
                if (modComp !== exceptModule) {
                    modComp.isBinding = false
                }
                for (settingComp in modComp.settingComponents) {
                    if (settingComp is KeybindComponent && settingComp !== exceptKeybind) {
                        settingComp.stopListening()
                    }
                }
            }
        }
    }

    override fun mouseClicked(event: MouseButtonEvent, bl: Boolean): Boolean {
        val mouseX = event.x()
        val mouseY = event.y()
        val button = normalizeButton(event.button())

        val picker = activeColorPicker
        if (picker != null) {
            if (!picker.mouseClicked(mouseX, mouseY, button)) {
                activeColorPicker = null
            }
            return true
        }

        for (i in panels.indices.reversed()) {
            val panel = panels[i]
            if (panel.mouseClicked(mouseX, mouseY, button)) {
                panels.removeAt(i)
                panels.add(panel)
                return true
            }
        }

        clearAllListening()
        return super.mouseClicked(event, bl)
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        val mouseX = event.x()
        val mouseY = event.y()
        val button = normalizeButton(event.button())
        activeColorPicker?.mouseReleased(mouseX, mouseY, button)
        for (panel in panels) {
            panel.mouseReleased(mouseX, mouseY, button)
        }
        return super.mouseReleased(event)
    }

    override fun mouseDragged(event: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean {
        val mouseX = event.x()
        val mouseY = event.y()
        val button = normalizeButton(event.button())
        if (activeColorPicker?.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) == true) {
            return true
        }
        for (panel in panels) {
            panel.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        }
        return super.mouseDragged(event, deltaX, deltaY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        if (scrollY != 0.0) {
            val hoveredPanel = panels.asReversed().firstOrNull { it.isMouseOver(mouseX, mouseY) }
            if (hoveredPanel != null) {
                hoveredPanel.mouseScrolled(scrollY)
            } else {
                for (panel in panels) {
                    panel.mouseScrolled(scrollY)
                }
            }
            return true
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }

    override fun keyPressed(event: KeyEvent): Boolean {
        val keyCode = event.key()
        val scanCode = event.keycode()
        val modifiers = event.modifiers()

        val picker = activeColorPicker
        if (picker != null) {
            if (keyCode == InputConstants.KEY_ESCAPE) {
                activeColorPicker = null
                return true
            }
            if (picker.keyPressed(keyCode, scanCode, modifiers)) {
                return true
            }
        }

        for (panel in panels) {
            if (panel.keyPressed(keyCode, scanCode, modifiers)) {
                return true
            }
        }

        if (keyCode == InputConstants.KEY_RSHIFT || (ClickGuiModule.key != InputConstants.UNKNOWN.value && keyCode == ClickGuiModule.key)) {
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
        clearAllListening()
        activeColorPicker = null
        val parent = parentScreen
        parentScreen = null
        ClickGuiModule.setEnabled(false)
        if (mc.gui.screen() === this) {
            mc.gui.setScreen(parent)
        }
    }
}
