package com.sixeyes.client.gui.material.components

import com.sixeyes.client.gui.material.AnimFloat
import com.sixeyes.client.gui.material.M3Theme
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

        val palette = M3Theme.current
        val toggleProgress = toggleAnim.update(if (boolSetting.value) 1f else 0f, dt)
        val switchW = 24f
        val switchH = 14f
        val switchX = x + width - switchW - 2f
        val switchY = y + (height - switchH) * 0.5f
        val maxTextW = switchX - (x + 2f) - 6f

        val fitted = fitText(boolSetting.name, maxTextW, baseSize = 10f, minSize = 8f)
        Fonts.google_sans_regular.drawString(
            matrix = matrix,
            text = fitted.text,
            x = x + 2f,
            y = switchY + (switchH - fitted.size) * 0.5f - 1.5f,
            size = fitted.size,
            color = palette.onSurface,
            shadow = false,
            pipeline = ClientRenderPipeline.WINDOW_TEXT
        )

        val offTrackColor = Color4b(54, 50, 64, 255)
        val trackColor = offTrackColor.interpolateTo(palette.primary, toggleProgress.toDouble())
        RenderUtils.roundedRect(
            matrix = matrix,
            x = switchX,
            y = switchY,
            width = switchW,
            height = switchH,
            color = trackColor,
            radius = switchH * 0.5f,
            pipeline = ClientRenderPipeline.WINDOW_RECT
        )

        val offThumbDiameter = 8f
        val onThumbDiameter = 10f
        val thumbDiameter = offThumbDiameter + (onThumbDiameter - offThumbDiameter) * toggleProgress
        val trackCapRadius = switchH * 0.5f
        val leftCapCenterX = switchX + trackCapRadius
        val rightCapCenterX = switchX + switchW - trackCapRadius
        val thumbCenterX = leftCapCenterX + (rightCapCenterX - leftCapCenterX) * toggleProgress
        val thumbCenterY = switchY + trackCapRadius
        val thumbX = thumbCenterX - thumbDiameter * 0.5f
        val thumbY = thumbCenterY - thumbDiameter * 0.5f

        val offThumbColor = palette.outline
        val onThumbColor = Color4b(34, 30, 39, 255)
        val thumbColor = offThumbColor.interpolateTo(onThumbColor, toggleProgress.toDouble())
        RenderUtils.roundedRect(
            matrix = matrix,
            x = thumbX,
            y = thumbY,
            width = thumbDiameter,
            height = thumbDiameter,
            color = thumbColor,
            radius = thumbDiameter * 0.5f,
            pipeline = ClientRenderPipeline.WINDOW_RECT
        )

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
