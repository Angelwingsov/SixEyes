package com.sixeyes.client.gui.material.components

import com.sixeyes.client.gui.material.AnimFloat
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.font.Fonts
import com.sixeyes.module.setting.BooleanSetting
import org.joml.Matrix4f

class BooleanComponent(private val boolSetting: BooleanSetting) : SettingComponent(boolSetting) {
    private val toggleAnim = AnimFloat(if (boolSetting.value) 1f else 0f, if (boolSetting.value) 1f else 0f, 16f)

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
        this.height = 18f
        val toggleProgress = toggleAnim.update(if (boolSetting.value) 1f else 0f, dt)
        val switchW = 24f
        val switchH = 14f
        val switchX = x + width - switchW - 2f
        val switchY = y + (height - switchH) * 0.5f
        val maxTextW = switchX - (x + 2f) - 6f
        var textSize = 10f
        val origW = Fonts.google_sans_regular.width(boolSetting.name, textSize)
        if (origW > maxTextW) {
            textSize = (textSize * (maxTextW / origW)).coerceIn(8f, 10f)
        }

        var displayText = boolSetting.name
        if (Fonts.google_sans_regular.width(displayText, textSize) > maxTextW) {
            while (displayText.isNotEmpty() && Fonts.google_sans_regular.width("$displayText...", textSize) > maxTextW) {
                displayText = displayText.dropLast(1)
            }
            displayText = "$displayText..."
        }

        Fonts.google_sans_regular.drawString(matrix = matrix, text = displayText, x = x + 2f, y = switchY + (switchH - textSize) * 0.5f - 1.5f, size = textSize, color = Color4b(230, 224, 233, 255), shadow = false, pipeline = ClientRenderPipeline.WINDOW_TEXT)
        val offTrackColor = Color4b(54, 50, 64, 255)
        val onTrackColor = Color4b(208, 188, 255, 255)
        val trackColor = offTrackColor.interpolateTo(onTrackColor, toggleProgress.toDouble())
        RenderUtils.roundedRect(matrix = matrix, x = switchX, y = switchY, width = switchW, height = switchH, color = trackColor, radius = switchH * 0.5f, pipeline = ClientRenderPipeline.WINDOW_RECT)
        val offThumbDiameter = 8f
        val onThumbDiameter = 10f
        val thumbDiameter = offThumbDiameter + (onThumbDiameter - offThumbDiameter) * toggleProgress
        val trackCapRadius = switchH * 0.5f
        val leftCapCenterX = switchX + trackCapRadius
        val rightCapCenterX = switchX + switchW - trackCapRadius
        val trackCenterY = switchY + trackCapRadius
        val thumbCenterX = leftCapCenterX + (rightCapCenterX - leftCapCenterX) * toggleProgress
        val thumbCenterY = trackCenterY
        val thumbX = thumbCenterX - thumbDiameter * 0.5f
        val thumbY = thumbCenterY - thumbDiameter * 0.5f
        val offThumbColor = Color4b(147, 143, 153, 255)
        val onThumbColor = Color4b(34, 30, 39, 255)
        val thumbColor = offThumbColor.interpolateTo(onThumbColor, toggleProgress.toDouble())
        RenderUtils.roundedRect(matrix = matrix, x = thumbX, y = thumbY, width = thumbDiameter, height = thumbDiameter, color = thumbColor, radius = thumbDiameter * 0.5f, pipeline = ClientRenderPipeline.WINDOW_RECT)

        return height
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 && isHovered(mouseX, mouseY, x, y, width, height)) {
            boolSetting.toggle()
            return true
        }
        return false
    }
}
