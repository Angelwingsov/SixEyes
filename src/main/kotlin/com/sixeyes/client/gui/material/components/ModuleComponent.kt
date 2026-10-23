package com.sixeyes.client.gui.material.components

import com.sixeyes.client.gui.material.M3Theme
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.font.Fonts
import com.sixeyes.module.Module
import com.sixeyes.module.setting.*
import org.joml.Matrix4f

class ModuleComponent(val module: Module) {
    var x: Float = 0f
    var y: Float = 0f
    var width: Float = 0f
    var height: Float = 0f
    var isExpanded: Boolean
        get() = module.isExpanded
        set(value) {
            module.isExpanded = value
        }

    val settingComponents: MutableList<SettingComponent> = ArrayList()

    init {
        for (setting in module.settings) {
            when (setting) {
                is BooleanSetting -> settingComponents.add(BooleanComponent(setting))
                is NumberSetting -> settingComponents.add(NumberComponent(setting))
                is ModeSetting -> settingComponents.add(ModeComponent(setting))
                is MultiModeSetting -> settingComponents.add(ModeComponent(setting))
                is ColorSetting -> settingComponents.add(ColorComponent(setting))
                is KeybindSetting -> settingComponents.add(KeybindComponent(setting))
            }
        }
    }

    fun hasSettings(): Boolean = settingComponents.isNotEmpty()

    fun render(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        mouseX: Int,
        mouseY: Int,
        dt: Float
    ): Float {
        this.x = x
        this.y = y
        this.width = width

        val palette = M3Theme.current
        val pillH = 22f
        val pillRadius = 6f
        var curY = y

        val isEnabled = module.isEnabled()
        val modBg = if (isEnabled) palette.primary else Color4b(43, 39, 52, 220)
        val modTextCol = if (isEnabled) Color4b(33, 0, 93, 255) else palette.onSurface

        RenderUtils.roundedRect(
            matrix = matrix,
            x = x,
            y = curY,
            width = width,
            height = pillH,
            color = modBg,
            radius = pillRadius,
            pipeline = ClientRenderPipeline.WINDOW_RECT
        )

        Fonts.google_sans_medium.drawString(
            matrix = matrix,
            text = module.name,
            x = x + 7f,
            y = curY + 6f,
            size = 10.5f,
            color = modTextCol,
            shadow = false,
            pipeline = ClientRenderPipeline.WINDOW_TEXT
        )

        curY += pillH

        if (isExpanded && hasSettings()) {
            curY += 6f
            for (comp in settingComponents) {
                if (comp.setting.isVisible()) {
                    val compH = comp.render(
                        matrix = matrix,
                        x = x,
                        y = curY,
                        width = width,
                        mouseX = mouseX,
                        mouseY = mouseY,
                        dt = dt
                    )
                    curY += compH + 6f
                }
            }
        }

        this.height = curY - y
        return height
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val pillH = 22f

        if (isHovered(mouseX, mouseY, x, y, width, pillH)) {
            if (button == 0) {
                module.toggle()
                return true
            } else if (button == 1 && hasSettings()) {
                isExpanded = !isExpanded
                return true
            }
        }

        if (isExpanded && hasSettings()) {
            for (comp in settingComponents) {
                if (comp.setting.isVisible() && comp.mouseClicked(mouseX, mouseY, button)) {
                    return true
                }
            }
        }

        return false
    }

    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        if (isExpanded) {
            for (comp in settingComponents) {
                if (comp.setting.isVisible()) {
                    comp.mouseReleased(mouseX, mouseY, button)
                }
            }
        }
    }

    fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double) {
        if (isExpanded) {
            for (comp in settingComponents) {
                if (comp.setting.isVisible()) {
                    comp.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
                }
            }
        }
    }

    fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (isExpanded) {
            for (comp in settingComponents) {
                if (comp.setting.isVisible() && comp.keyPressed(keyCode, scanCode, modifiers)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isHovered(mouseX: Double, mouseY: Double, x: Float, y: Float, w: Float, h: Float): Boolean {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h
    }
}
