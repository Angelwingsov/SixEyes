package com.sixeyes.client.gui.material.components

import com.mojang.blaze3d.platform.InputConstants
import com.sixeyes.client.gui.material.ClickGuiScreen
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
    var isBinding: Boolean = false
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

        val hasBind = module.key != InputConstants.UNKNOWN.value
        val showBindBadge = isBinding || hasBind
        var keyText = ""
        var badgeX = 0f
        var badgeW = 0f

        if (showBindBadge) {
            keyText = if (isBinding) "..." else KeybindSetting.formatKey(module.key)
            val keyFontSize = 8.5f
            val keyTextW = Fonts.google_sans_medium.width(keyText, keyFontSize)
            val badgePadX = 4.5f
            badgeW = (keyTextW + badgePadX * 2f).coerceAtLeast(18f)
            val badgeH = 14f
            badgeX = x + width - badgeW - 4.5f
            val badgeY = curY + (pillH - badgeH) * 0.5f

            val badgeBg = if (isEnabled) {
                if (isBinding) Color4b(33, 0, 93, 75) else Color4b(33, 0, 93, 40)
            } else {
                if (isBinding) palette.primaryContainer else palette.surfaceContainerHigh
            }
            val badgeTextCol = if (isEnabled) {
                Color4b(33, 0, 93, 255)
            } else {
                if (isBinding) palette.onPrimaryContainer else palette.primary
            }

            RenderUtils.roundedRect(
                matrix = matrix,
                x = badgeX,
                y = badgeY,
                width = badgeW,
                height = badgeH,
                color = badgeBg,
                radius = 4f,
                pipeline = ClientRenderPipeline.WINDOW_RECT
            )

            Fonts.google_sans_medium.drawCenteredString(
                matrix = matrix,
                text = keyText,
                centerX = badgeX + badgeW * 0.5f,
                y = badgeY + 2.5f,
                size = keyFontSize,
                color = badgeTextCol,
                shadow = false,
                pipeline = ClientRenderPipeline.WINDOW_TEXT
            )
        }

        val isLongBind = showBindBadge && (keyText.length > 2 && keyText != "..." || badgeW > 24f)
        val nameWAtDefault = Fonts.google_sans_medium.width(module.name, 10.5f)
        val shouldAlignLeft = showBindBadge && (isLongBind || ((x + width * 0.5f) + nameWAtDefault * 0.5f > badgeX - 6f))

        val maxNameWidth = if (shouldAlignLeft) {
            (badgeX - (x + 7f) - 4f).coerceAtLeast(20f)
        } else if (showBindBadge) {
            val spaceToBadge = badgeX - (x + width * 0.5f) - 3f
            if (spaceToBadge > 0f) minOf(width - 14f, (spaceToBadge * 2f).coerceAtLeast(20f)) else width - 14f
        } else {
            width - 14f
        }

        var nameSize = 10.5f
        val origNameW = Fonts.google_sans_medium.width(module.name, nameSize)
        if (origNameW > maxNameWidth && origNameW > 0f) {
            nameSize = (nameSize * (maxNameWidth / origNameW)).coerceIn(8f, 10.5f)
        }

        val textY = curY + (pillH - nameSize) * 0.5f - 0.5f

        if (shouldAlignLeft) {
            Fonts.google_sans_medium.drawString(
                matrix = matrix,
                text = module.name,
                x = x + 7f,
                y = textY,
                size = nameSize,
                color = modTextCol,
                shadow = false,
                pipeline = ClientRenderPipeline.WINDOW_TEXT
            )
        } else {
            Fonts.google_sans_medium.drawCenteredString(
                matrix = matrix,
                text = module.name,
                centerX = x + width * 0.5f,
                y = textY,
                size = nameSize,
                color = modTextCol,
                shadow = false,
                pipeline = ClientRenderPipeline.WINDOW_TEXT
            )
        }

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
            when (button) {
                0 -> {
                    ClickGuiScreen.clearAllListening()
                    module.toggle()
                    return true
                }
                1 -> {
                    ClickGuiScreen.clearAllListening()
                    if (hasSettings()) {
                        isExpanded = !isExpanded
                    }
                    return true
                }
                2 -> {
                    val nextBinding = !isBinding
                    ClickGuiScreen.clearAllListening()
                    isBinding = nextBinding
                    return true
                }
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
        if (isBinding) {
            if (keyCode == InputConstants.KEY_ESCAPE || keyCode == InputConstants.KEY_DELETE || keyCode == InputConstants.KEY_BACKSPACE) {
                module.key = InputConstants.UNKNOWN.value
            } else {
                module.key = keyCode
            }
            isBinding = false
            return true
        }

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
