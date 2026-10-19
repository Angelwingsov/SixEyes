package com.sixeyes.client.gui.material.components

import com.sixeyes.client.gui.material.AnimFloat
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.font.Fonts
import com.sixeyes.module.setting.NumberSetting
import org.joml.Matrix4f

class NumberComponent(private val numSetting: NumberSetting) : SettingComponent(numSetting) {
    private var isDragging = false
    private val thumbAnim = AnimFloat(0f, 0f, 16f)

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

        val hovered = isHovered(mouseX.toDouble(), mouseY.toDouble(), x, y, width, height)
        val expandThumb = thumbAnim.update(if (hovered || isDragging) 1f else 0f, dt)

        val valText = numSetting.getFormattedValue()
        val valW = Fonts.google_sans_medium.width(valText, 10f)
        val maxTextW = width - valW - 8f

        var textSize = 10f
        val origW = Fonts.google_sans_regular.width(numSetting.name, textSize)
        if (origW > maxTextW) {
            textSize = (textSize * (maxTextW / origW)).coerceIn(8f, 10f)
        }

        var displayText = numSetting.name
        if (Fonts.google_sans_regular.width(displayText, textSize) > maxTextW) {
            while (displayText.isNotEmpty() && Fonts.google_sans_regular.width("$displayText...", textSize) > maxTextW) {
                displayText = displayText.dropLast(1)
            }
            displayText = "$displayText..."
        }

        Fonts.google_sans_regular.drawString(
            matrix = matrix,
            text = displayText,
            x = x + 2f,
            y = y + 2f,
            size = textSize,
            color = Color4b(230, 224, 233, 255),
            shadow = false,
            pipeline = ClientRenderPipeline.WINDOW_TEXT
        )

        Fonts.google_sans_medium.drawRightString(
            matrix = matrix,
            text = valText,
            rightX = x + width - 2f,
            y = y + 2f,
            size = 10f,
            color = Color4b(208, 188, 255, 255),
            shadow = false,
            pipeline = ClientRenderPipeline.WINDOW_TEXT
        )

        val trackX = x + 2f
        val trackY = y + 17f
        val trackW = width - 4f
        val trackH = 3.5f
        val trackRadius = 1.75f

        val range = (numSetting.max - numSetting.min).coerceAtLeast(0.0001)
        val percentage = ((numSetting.value - numSetting.min) / range).coerceIn(0.0, 1.0).toFloat()
        val fillW = trackW * percentage
        RenderUtils.roundedRect(matrix = matrix, x = trackX, y = trackY, width = trackW, height = trackH, color = Color4b(54, 50, 64, 255), radius = trackRadius, pipeline = ClientRenderPipeline.WINDOW_RECT)

        if (fillW > 0.5f) {
            RenderUtils.roundedRect(matrix = matrix, x = trackX, y = trackY, width = fillW, height = trackH, color = Color4b(208, 188, 255, 255), radius = trackRadius, pipeline = ClientRenderPipeline.WINDOW_RECT)
        }

        val thumbBaseSize = 8f
        val thumbSize = thumbBaseSize + expandThumb * 2f
        val thumbX = trackX + fillW - thumbSize * 0.5f
        val thumbY = trackY + (trackH - thumbSize) * 0.5f
        RenderUtils.roundedRect(matrix = matrix, x = thumbX, y = thumbY, width = thumbSize, height = thumbSize, color = Color4b(208, 188, 255, 255), radius = thumbSize * 0.5f, pipeline = ClientRenderPipeline.WINDOW_RECT)

        if (isDragging) {
            updateValueFromMouse(mouseX.toDouble(), trackX, trackW)
        }

        return height
    }

    private fun updateValueFromMouse(mouseX: Double, trackX: Float, trackW: Float) {
        val pct = ((mouseX - trackX) / trackW).coerceIn(0.0, 1.0)
        val rawVal = numSetting.min + pct * (numSetting.max - numSetting.min)
        numSetting.set(rawVal)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 && isHovered(mouseX, mouseY, x, y, width, height)) {
            isDragging = true
            updateValueFromMouse(mouseX, x + 2f, width - 4f)
            return true
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        if (button == 0) {
            isDragging = false
        }
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double) {
        if (isDragging && button == 0) {
            updateValueFromMouse(mouseX, x + 2f, width - 4f)
        }
    }
}
