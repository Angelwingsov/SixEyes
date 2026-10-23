package com.sixeyes.client.gui.material.components

import com.sixeyes.client.extensions.mc
import com.sixeyes.client.gui.material.M3Theme
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.font.Fonts
import com.sixeyes.module.setting.ColorSetting
import org.joml.Matrix4f

class ColorPickerWindow(
    val setting: ColorSetting,
    var x: Float,
    var y: Float,
    val width: Float = 140f
) {
    var height: Float = if (setting.hasAlpha) 178f else 162f

    private enum class Drag { NONE, WINDOW, SV, HUE, ALPHA }
    private var drag = Drag.NONE
    private var dragX = 0f
    private var dragY = 0f

    private var hue = 0f
    private var sat = 1f
    private var bri = 1f
    private var alpha = 1f
    private var lastColor: Color4b? = null
    private val hsbBuffer = FloatArray(3)

    init {
        sync()
    }

    private fun sync() {
        val col = setting.value
        if (lastColor == null || col != lastColor) {
            lastColor = col
            val hsb = setting.getHSB(hsbBuffer)
            val h = hsb[0]
            val s = hsb[1]
            val b = hsb[2]
            if (s > 0.001f && b > 0.001f) hue = h
            sat = s
            bri = b
            alpha = col.a / 255f
        }
    }

    private fun applyColor() {
        lastColor = Color4b.ofHSB(hue, sat, bri, alpha).also { setting.set(it) }
    }

    fun render(matrix: Matrix4f, mouseX: Int, mouseY: Int, dt: Float) {
        sync()
        val palette = M3Theme.current

        RenderUtils.roundedRect(matrix, x - 3f, y - 3f, width + 6f, height + 6f, Color4b(0, 0, 0, 110), 10f, ClientRenderPipeline.WINDOW_RECT)
        RenderUtils.roundedRect(matrix, x, y, width, height, palette.surface, 8f, ClientRenderPipeline.WINDOW_RECT)

        RenderUtils.roundedRect(matrix, x + 4f, y + 4f, width - 8f, 20f, palette.surfaceContainer, 6f, ClientRenderPipeline.WINDOW_RECT)
        Fonts.google_sans_medium.drawCenteredString(matrix, setting.name, x + width * 0.5f, y + 9f, 10.5f, palette.onSurface, false, ClientRenderPipeline.WINDOW_TEXT)

        val svX = x + 6f
        val svY = y + 29f
        val svSize = width - 29f
        val hueX = svX + svSize + 5f
        val alphaY = svY + svSize + 5f
        val alphaW = width - 12f

        RenderUtils.gradientRect(
            matrix, svX, svY, svSize, svSize,
            Color4b.WHITE, Color4b.ofHSB(hue, 1f, 1f), Color4b.BLACK, Color4b.BLACK,
            ClientRenderPipeline.WINDOW_RECT
        )
        val curX = svX + sat * svSize
        val curY = svY + (1f - bri) * svSize
        RenderUtils.roundedRect(matrix, curX - 3.5f, curY - 3.5f, 7f, 7f, palette.surface, 3.5f, ClientRenderPipeline.WINDOW_RECT)
        RenderUtils.roundedRect(matrix, curX - 2.5f, curY - 2.5f, 5f, 5f, Color4b.WHITE, 2.5f, ClientRenderPipeline.WINDOW_RECT)

        val segH = svSize / 12f
        for (i in 0 until 12) {
            val c1 = Color4b.ofHSB(i / 12f, 1f, 1f)
            val c2 = Color4b.ofHSB((i + 1) / 12f, 1f, 1f)
            RenderUtils.gradientRect(matrix, hueX, svY + i * segH, 12f, segH, c1, c1, c2, c2, ClientRenderPipeline.WINDOW_RECT)
        }
        val hueIndicatorY = (svY + hue * svSize).coerceIn(svY, svY + svSize)
        RenderUtils.roundedRect(matrix, hueX - 1f, hueIndicatorY - 2f, 14f, 4f, palette.surface, 2f, ClientRenderPipeline.WINDOW_RECT)
        RenderUtils.roundedRect(matrix, hueX, hueIndicatorY - 1.5f, 12f, 3f, Color4b.WHITE, 1.5f, ClientRenderPipeline.WINDOW_RECT)

        var nextY = svY + svSize + 5f
        if (setting.hasAlpha) {
            val cols = (alphaW / 6f).toInt() + 1
            for (cIdx in 0 until cols) {
                val cx = svX + cIdx * 6f
                val cw = 6f.coerceAtMost(svX + alphaW - cx)
                if (cw <= 0f) continue
                val topLight = cIdx % 2 == 0
                RenderUtils.rect(matrix, cx, alphaY, cw, 5f, if (topLight) palette.surfaceBright else palette.surfaceContainer, ClientRenderPipeline.WINDOW_RECT)
                RenderUtils.rect(matrix, cx, alphaY + 5f, cw, 5f, if (topLight) palette.surfaceContainer else palette.surfaceBright, ClientRenderPipeline.WINDOW_RECT)
            }
            val col = setting.value
            RenderUtils.gradientRect(
                matrix, svX, alphaY, alphaW, 10f,
                Color4b(col.r, col.g, col.b, 0), Color4b(col.r, col.g, col.b, 255),
                Color4b(col.r, col.g, col.b, 255), Color4b(col.r, col.g, col.b, 0),
                ClientRenderPipeline.WINDOW_RECT
            )
            val alphaIndicatorX = (svX + alpha * alphaW).coerceIn(svX, svX + alphaW)
            RenderUtils.roundedRect(matrix, alphaIndicatorX - 2f, alphaY - 1f, 4f, 12f, palette.surface, 2f, ClientRenderPipeline.WINDOW_RECT)
            RenderUtils.roundedRect(matrix, alphaIndicatorX - 1.5f, alphaY, 3f, 10f, Color4b.WHITE, 1.5f, ClientRenderPipeline.WINDOW_RECT)
            nextY += 15f
        }

        RenderUtils.roundedRect(matrix, svX, nextY, 28f, 14f, setting.value, 3f, ClientRenderPipeline.WINDOW_RECT)
        Fonts.google_sans_medium.drawString(matrix, setting.toHex(), svX + 36f, nextY + 2f, 9.5f, palette.primary, false, ClientRenderPipeline.WINDOW_TEXT)

        this.height = nextY + 14f + 6f - y
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0 || !isHovered(mouseX, mouseY, x, y, width, height)) return false

        val svX = x + 6f
        val svY = y + 29f
        val svSize = width - 29f
        val hueX = svX + svSize + 5f
        val alphaY = svY + svSize + 5f
        val alphaW = width - 12f

        when {
            isHovered(mouseX, mouseY, x, y, width, 26f) -> {
                drag = Drag.WINDOW
                dragX = (mouseX - x).toFloat()
                dragY = (mouseY - y).toFloat()
            }
            isHovered(mouseX, mouseY, svX, svY, svSize, svSize) -> {
                drag = Drag.SV
                updateSV(mouseX, mouseY)
            }
            isHovered(mouseX, mouseY, hueX - 2f, svY, 16f, svSize) -> {
                drag = Drag.HUE
                updateHue(mouseY)
            }
            setting.hasAlpha && isHovered(mouseX, mouseY, svX, alphaY - 2f, alphaW, 14f) -> {
                drag = Drag.ALPHA
                updateAlpha(mouseX)
            }
        }
        return true
    }

    fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (button != 0 || drag == Drag.NONE) return false
        when (drag) {
            Drag.WINDOW -> {
                x = (mouseX - dragX).toFloat().coerceIn(4f, mc.window.guiScaledWidth - width - 4f)
                y = (mouseY - dragY).toFloat().coerceIn(4f, mc.window.guiScaledHeight - height - 4f)
            }
            Drag.SV -> updateSV(mouseX, mouseY)
            Drag.HUE -> updateHue(mouseY)
            Drag.ALPHA -> updateAlpha(mouseX)
            Drag.NONE -> {}
        }
        return true
    }

    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        if (button == 0) drag = Drag.NONE
    }

    fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean = false

    private fun updateSV(mouseX: Double, mouseY: Double) {
        val svX = x + 6f
        val svY = y + 29f
        val svSize = width - 29f
        sat = ((mouseX - svX) / svSize).toFloat().coerceIn(0f, 1f)
        bri = (1f - (mouseY - svY) / svSize).toFloat().coerceIn(0f, 1f)
        applyColor()
    }

    private fun updateHue(mouseY: Double) {
        val svY = y + 29f
        val svSize = width - 29f
        hue = ((mouseY - svY) / svSize).toFloat().coerceIn(0f, 1f)
        applyColor()
    }

    private fun updateAlpha(mouseX: Double) {
        val svX = x + 6f
        val alphaW = width - 12f
        alpha = ((mouseX - svX) / alphaW).toFloat().coerceIn(0f, 1f)
        applyColor()
    }

    private fun isHovered(mouseX: Double, mouseY: Double, x: Float, y: Float, w: Float, h: Float): Boolean =
        mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h
}
