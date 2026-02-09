package com.sixeyes.client.util.render

import org.joml.Vector4f
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.render.main.ChromaRenderer
import com.sixeyes.client.render.main.scissor.ScissorRect

object ScissorUtil {
    private val NO_SCISSOR = Vector4f(-100f, -100f, 100f, 100f)
    private val CACHE_SCISSOR = Vector4f(-1f)

    fun start(x: Float, y: Float, width: Float, height: Float) {
        ChromaRenderer.scissorStack.push(ScissorRect(x, y, width, height))
    }

    fun end() {
        ChromaRenderer.scissorStack.pop()
    }

    fun getCurrentScissorValues(): Vector4f {
        if (ChromaRenderer.scissorStack.current == null) {
            return NO_SCISSOR
        }

        val rect = ChromaRenderer.scissorStack.current

        val scale = mc.window.guiScale.toFloat()
        val h = mc.window.height.toFloat()

        val realX = rect.x() * scale
        val realY = rect.y() * scale
        val realW = rect.width() * scale
        val realH = rect.height() * scale

        val glMinY = h - (realY + realH)

        val glMaxY = h - realY

        val glMinX = realX
        val glMaxX = realX + realW

        CACHE_SCISSOR.set(glMinX, glMinY, glMaxX, glMaxY)
        return CACHE_SCISSOR
    }
}