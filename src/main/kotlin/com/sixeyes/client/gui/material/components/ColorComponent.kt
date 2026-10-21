package com.sixeyes.client.gui.material.components

import com.sixeyes.client.extensions.mc
import com.sixeyes.client.gui.material.AnimFloat
import com.sixeyes.client.gui.material.ClickGuiScreen
import com.sixeyes.client.gui.material.M3Theme
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.font.Fonts
import com.sixeyes.module.setting.ColorSetting
import org.joml.Matrix4f

class ColorComponent(private val colorSetting: ColorSetting) : SettingComponent(colorSetting) {
    private val hoverAnim = AnimFloat(0f, 0f, 14f)

    override fun render(
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
        this.height = 20f

        val palette = M3Theme.current
        val hovered = isHovered(mouseX.toDouble(), mouseY.toDouble(), x, y, width, height)
        val hoverAlpha = hoverAnim.update(if (hovered) 1f else 0f, dt)

        if (hoverAlpha > 0.01f) {
            RenderUtils.roundedRect(
                matrix = matrix,
                x = x + 2f,
                y = y + 1f,
                width = width - 4f,
                height = height - 2f,
                color = palette.stateHover.alpha((palette.stateHover.a * hoverAlpha).toInt()),
                radius = 4f,
                pipeline = ClientRenderPipeline.WINDOW_RECT
            )
        }

        val swatchW = 24f
        val swatchH = 12f
        val swatchX = x + width - swatchW - 4f
        val swatchY = y + (height - swatchH) * 0.5f

        val maxTextW = swatchX - (x + 4f) - 4f
        var textSize = 10f
        val origW = Fonts.google_sans_regular.width(colorSetting.name, textSize)
        if (origW > maxTextW) {
            textSize = (textSize * (maxTextW / origW)).coerceIn(8f, 10f)
        }

        var displayText = colorSetting.name
        if (Fonts.google_sans_regular.width(displayText, textSize) > maxTextW) {
            while (displayText.isNotEmpty() && Fonts.google_sans_regular.width("$displayText...", textSize) > maxTextW) {
                displayText = displayText.dropLast(1)
            }
            displayText = "$displayText..."
        }

        Fonts.google_sans_regular.drawString(
            matrix = matrix,
            text = displayText,
            x = x + 4f,
            y = y + (height - textSize) * 0.5f - 1f,
            size = textSize,
            color = palette.onSurface,
            shadow = false,
            pipeline = ClientRenderPipeline.WINDOW_TEXT
        )

        RenderUtils.roundedRect(
            matrix = matrix,
            x = swatchX,
            y = swatchY,
            width = swatchW,
            height = swatchH,
            color = colorSetting.value,
            radius = 3f,
            pipeline = ClientRenderPipeline.WINDOW_RECT
        )

        return height
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 && isHovered(mouseX, mouseY, x, y, width, height)) {
            if (ClickGuiScreen.activeColorPicker?.setting == colorSetting) {
                ClickGuiScreen.activeColorPicker = null
            } else {
                val picker = ColorPickerWindow(
                    setting = colorSetting,
                    x = 0f,
                    y = 0f
                )
                var winX = x + width + 8f
                if (winX + picker.width > mc.window.guiScaledWidth - 10f) {
                    winX = x - picker.width - 8f
                }
                if (winX < 10f) {
                    winX = ((mc.window.guiScaledWidth - picker.width) * 0.5f).coerceAtLeast(10f)
                }
                val winY = y.coerceIn(10f, (mc.window.guiScaledHeight - picker.height - 10f).coerceAtLeast(10f))
                picker.x = winX
                picker.y = winY

                ClickGuiScreen.activeColorPicker = picker
            }
            return true
        }
        return false
    }
}
