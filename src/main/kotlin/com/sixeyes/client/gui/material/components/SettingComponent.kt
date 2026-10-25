package com.sixeyes.client.gui.material.components

import com.sixeyes.client.util.render.font.Font
import com.sixeyes.client.util.render.font.Fonts
import com.sixeyes.module.setting.Setting
import org.joml.Matrix4f

abstract class SettingComponent(val setting: Setting<*>) : GuiComponent {
    override var x: Float = 0f
    override var y: Float = 0f
    override var width: Float = 0f
    override var height: Float = 0f

    protected data class FittedText(val text: String, val size: Float)

    abstract fun render(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        mouseX: Int,
        mouseY: Int,
        dt: Float
    ): Float

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean = false
    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {}
    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double) {}
    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean = false
    open fun charTyped(codePoint: Char, modifiers: Int): Boolean = false

    protected fun isHovered(mouseX: Double, mouseY: Double, x: Float, y: Float, w: Float, h: Float): Boolean {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h
    }

    protected fun fitText(
        text: String,
        maxWidth: Float,
        baseSize: Float = 10f,
        minSize: Float = 8f,
        font: Font = Fonts.google_sans_regular
    ): FittedText {
        if (maxWidth <= 0f || text.isEmpty()) return FittedText("", minSize)

        var textSize = baseSize
        val origW = font.width(text, textSize)
        if (origW > maxWidth) {
            textSize = (textSize * (maxWidth / origW)).coerceIn(minSize, baseSize)
        }

        if (font.width(text, textSize) <= maxWidth) {
            return FittedText(text, textSize)
        }

        var truncated = text
        while (truncated.isNotEmpty() && font.width("$truncated...", textSize) > maxWidth) {
            truncated = truncated.dropLast(1)
        }
        return FittedText("$truncated...", textSize)
    }
}
