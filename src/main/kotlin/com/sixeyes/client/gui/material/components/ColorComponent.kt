package com.sixeyes.client.gui.material.components

import com.sixeyes.client.gui.material.AnimFloat
import com.sixeyes.client.gui.material.M3Theme
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.ScissorUtil
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.font.Fonts
import com.sixeyes.module.setting.ColorSetting
import org.joml.Matrix4f
import java.awt.Color

class ColorComponent(private val colorSetting: ColorSetting) : SettingComponent(colorSetting) {
    private var isExpanded = false
    private val expandAnim = AnimFloat(0f, 0f, 16f)
    private val hoverAnim = AnimFloat(0f, 0f, 14f)

    private var draggingHue = false
    private var draggingSaturation = false
    private var draggingAlpha = false

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

        val baseH = 26f
        val expandedContentH = if (colorSetting.hasAlpha) 66f else 48f
        val expandProgress = expandAnim.update(if (isExpanded) 1f else 0f, dt)
        val currentBodyH = expandedContentH * expandProgress

        this.height = baseH + currentBodyH

        val palette = M3Theme.current
        val hovered = isHovered(mouseX.toDouble(), mouseY.toDouble(), x, y, width, baseH)
        val hoverAlpha = hoverAnim.update(if (hovered) 1f else 0f, dt)

        val swatchW = 28f
        val swatchH = 14f
        val swatchX = x + width - swatchW - 8f
        val swatchY = y + (baseH - swatchH) * 0.5f

        val maxTextW = swatchX - (x + 8f) - 4f
        var textSize = 11f
        val origW = Fonts.google_sans_regular.width(colorSetting.name, textSize)
        if (origW > maxTextW) {
            textSize = (textSize * (maxTextW / origW)).coerceIn(8f, 11f)
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
            x = x + 8f,
            y = y + 7f,
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
            radius = 4f,
            pipeline = ClientRenderPipeline.WINDOW_RECT
        )

        if (currentBodyH > 1f) {
            ScissorUtil.start(x + 6f, y + baseH, width - 12f, currentBodyH)

            RenderUtils.roundedRect(
                matrix = matrix,
                x = x + 6f,
                y = y + baseH,
                width = width - 12f,
                height = expandedContentH,
                color = palette.surfaceContainerLow,
                radius = 8f,
                pipeline = ClientRenderPipeline.WINDOW_RECT
            )

            val hsb = FloatArray(3)
            Color.RGBtoHSB(colorSetting.value.r, colorSetting.value.g, colorSetting.value.b, hsb)
            var hue = hsb[0]
            var sat = hsb[1]
            var bri = hsb[2]
            var alphaVal = colorSetting.value.a / 255f

            val sliderX = x + 12f
            val sliderW = width - 24f
            val sliderH = 6f
            val sliderRadius = 3f

            var curY = y + baseH + 8f

            val segCount = 12
            val segW = sliderW / segCount
            for (i in 0 until segCount) {
                val h1 = i / segCount.toFloat()
                val h2 = (i + 1) / segCount.toFloat()
                val c1 = Color4b.ofHSB(h1, 1f, 1f)
                val c2 = Color4b.ofHSB(h2, 1f, 1f)
                RenderUtils.gradientRect(
                    matrix = matrix,
                    x = sliderX + i * segW,
                    y = curY,
                    width = segW,
                    height = sliderH,
                    topLeft = c1,
                    topRight = c2,
                    bottomRight = c2,
                    bottomLeft = c1,
                    pipeline = ClientRenderPipeline.WINDOW_RECT
                )
            }

            val hueThumbX = sliderX + hue * sliderW - 3f
            RenderUtils.roundedRect(
                matrix = matrix,
                x = hueThumbX,
                y = curY - 2f,
                width = 6f,
                height = sliderH + 4f,
                color = Color4b.WHITE,
                radius = 3f,
                pipeline = ClientRenderPipeline.WINDOW_RECT
            )

            if (draggingHue) {
                val pct = ((mouseX - sliderX) / sliderW).coerceIn(0f, 1f)
                hue = pct
                val newCol = Color4b.ofHSB(hue, sat, bri, alphaVal)
                colorSetting.set(newCol)
            }

            curY += sliderH + 12f

            val pureColor = Color4b.ofHSB(hue, 1f, 1f)
            RenderUtils.gradientRect(
                matrix = matrix,
                x = sliderX,
                y = curY,
                width = sliderW,
                height = sliderH,
                topLeft = Color4b.WHITE,
                topRight = pureColor,
                bottomRight = pureColor,
                bottomLeft = Color4b.WHITE,
                pipeline = ClientRenderPipeline.WINDOW_RECT
            )

            val satThumbX = sliderX + sat * sliderW - 3f
            RenderUtils.roundedRect(
                matrix = matrix,
                x = satThumbX,
                y = curY - 2f,
                width = 6f,
                height = sliderH + 4f,
                color = Color4b.WHITE,
                radius = 3f,
                pipeline = ClientRenderPipeline.WINDOW_RECT
            )

            if (draggingSaturation) {
                val pct = ((mouseX - sliderX) / sliderW).coerceIn(0f, 1f)
                sat = pct
                val newCol = Color4b.ofHSB(hue, sat, bri, alphaVal)
                colorSetting.set(newCol)
            }

            curY += sliderH + 12f

            if (colorSetting.hasAlpha) {
                val rgbOpaque = Color4b(colorSetting.value.r, colorSetting.value.g, colorSetting.value.b, 255)
                val rgbTrans = Color4b(colorSetting.value.r, colorSetting.value.g, colorSetting.value.b, 0)

                RenderUtils.gradientRect(
                    matrix = matrix,
                    x = sliderX,
                    y = curY,
                    width = sliderW,
                    height = sliderH,
                    topLeft = rgbTrans,
                    topRight = rgbOpaque,
                    bottomRight = rgbOpaque,
                    bottomLeft = rgbTrans,
                    pipeline = ClientRenderPipeline.WINDOW_RECT
                )

                val alphaThumbX = sliderX + alphaVal * sliderW - 3f
                RenderUtils.roundedRect(
                    matrix = matrix,
                    x = alphaThumbX,
                    y = curY - 2f,
                    width = 6f,
                    height = sliderH + 4f,
                    color = Color4b.WHITE,
                    radius = 3f,
                    pipeline = ClientRenderPipeline.WINDOW_RECT
                )

                if (draggingAlpha) {
                    val pct = ((mouseX - sliderX) / sliderW).coerceIn(0f, 1f)
                    alphaVal = pct
                    val newCol = Color4b.ofHSB(hue, sat, bri, alphaVal)
                    colorSetting.set(newCol)
                }
            }

            ScissorUtil.end()
        }

        return height
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val baseH = 26f
        if (button == 0) {
            if (isHovered(mouseX, mouseY, x, y, width, baseH)) {
                isExpanded = !isExpanded
                return true
            }

            if (isExpanded) {
                val sliderX = x + 12f
                val sliderW = width - 24f
                val sliderH = 10f

                var curY = y + baseH + 6f
                if (isHovered(mouseX, mouseY, sliderX, curY, sliderW, sliderH)) {
                    draggingHue = true
                    return true
                }

                curY += 18f
                if (isHovered(mouseX, mouseY, sliderX, curY, sliderW, sliderH)) {
                    draggingSaturation = true
                    return true
                }

                curY += 18f
                if (colorSetting.hasAlpha && isHovered(mouseX, mouseY, sliderX, curY, sliderW, sliderH)) {
                    draggingAlpha = true
                    return true
                }
            }
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        if (button == 0) {
            draggingHue = false
            draggingSaturation = false
            draggingAlpha = false
        }
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double) {
    }
}
