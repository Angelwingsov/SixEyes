package com.sixeyes.client.gui.material.components

import com.mojang.blaze3d.platform.InputConstants
import com.sixeyes.client.gui.material.AnimFloat
import com.sixeyes.client.gui.material.ClickGuiScreen
import com.sixeyes.client.gui.material.M3Theme
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.font.Fonts
import com.sixeyes.module.setting.KeybindSetting
import org.joml.Matrix4f

class KeybindComponent(private val keySetting: KeybindSetting) : SettingComponent(keySetting) {
    var isListening = false
        private set
    private val hoverAnim = AnimFloat(0f, 0f, 14f)

    fun stopListening() {
        isListening = false
    }

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
        this.height = 26f

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
                radius = 6f,
                pipeline = ClientRenderPipeline.WINDOW_RECT
            )
        }

        val keyText = if (isListening) "..." else keySetting.getKeyName()
        val textW = Fonts.google_sans_medium.width(keyText, 10f)
        val chipPad = 8f
        val chipW = (textW + chipPad * 2).coerceAtLeast(34f)
        val chipH = 18f
        val chipX = x + width - chipW - 8f
        val chipY = y + (height - chipH) * 0.5f

        val maxTextW = chipX - (x + 8f) - 4f
        val fitted = fitText(keySetting.name, maxTextW, baseSize = 11f, minSize = 8f)

        Fonts.google_sans_regular.drawString(
            matrix = matrix,
            text = fitted.text,
            x = x + 8f,
            y = y + 7f,
            size = fitted.size,
            color = palette.onSurface,
            shadow = false,
            pipeline = ClientRenderPipeline.WINDOW_TEXT
        )

        val chipBg = if (isListening) palette.primaryContainer else palette.surfaceContainerHigh
        val chipTextCol = if (isListening) palette.onPrimaryContainer else palette.primary

        RenderUtils.roundedRect(
            matrix = matrix,
            x = chipX,
            y = chipY,
            width = chipW,
            height = chipH,
            color = chipBg,
            radius = 6f,
            pipeline = ClientRenderPipeline.WINDOW_RECT
        )

        Fonts.google_sans_medium.drawCenteredString(
            matrix = matrix,
            text = keyText,
            centerX = chipX + chipW * 0.5f,
            y = chipY + 4f,
            size = 10f,
            color = chipTextCol,
            shadow = false,
            pipeline = ClientRenderPipeline.WINDOW_TEXT
        )

        return height
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if ((button == 0 || button == 2) && isHovered(mouseX, mouseY, x, y, width, height)) {
            val nextListening = !isListening
            ClickGuiScreen.clearAllListening()
            isListening = nextListening
            return true
        } else if (isListening) {
            isListening = false
        }
        return false
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (isListening) {
            if (keyCode == InputConstants.KEY_ESCAPE || keyCode == InputConstants.KEY_BACKSPACE || keyCode == InputConstants.KEY_DELETE) {
                keySetting.set(InputConstants.UNKNOWN.value)
            } else {
                keySetting.set(keyCode)
            }
            isListening = false
            return true
        }
        return false
    }
}
