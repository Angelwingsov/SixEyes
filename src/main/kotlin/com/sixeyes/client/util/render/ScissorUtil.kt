package com.sixeyes.client.util.render

import com.sixeyes.client.extensions.mc
import com.sixeyes.client.render.main.ChromaRenderer
import org.joml.Vector4f
import kotlin.math.ceil
import kotlin.math.floor

object ScissorUtil {
    private val NO_SCISSOR = Vector4f(-100000f, 0f, 0f, 0f)
    private val EMPTY_SCISSOR = Vector4f(0f, 0f, 0f, 0f)
    private val CACHE_SCISSOR = Vector4f(-1f)

    fun start(x: Float, y: Float, width: Float, height: Float) {
        ChromaRenderer.scissorStack.push(x, y, width, height)
        val gfx = RenderUtils.currentGraphics
        if (gfx != null) {
            val cur = ChromaRenderer.scissorStack.current
            if (cur != null) {
                gfx.enableScissor(
                    floor(cur.x()).toInt(),
                    floor(cur.y()).toInt(),
                    ceil(cur.x() + cur.width()).toInt(),
                    ceil(cur.y() + cur.height()).toInt()
                )
            }
        }
    }

    fun end() {
        ChromaRenderer.scissorStack.pop()
        RenderUtils.currentGraphics?.disableScissor()
    }

    inline fun <T> withScissor(x: Float, y: Float, width: Float, height: Float, block: () -> T): T {
        start(x, y, width, height)
        try {
            return block()
        } finally {
            end()
        }
    }

    fun getCurrentScissorValues(): Vector4f {
        val rect = ChromaRenderer.scissorStack.current ?: return NO_SCISSOR
        if (rect.width() <= 0f || rect.height() <= 0f) {
            return EMPTY_SCISSOR
        }

        val scale = mc.window.guiScale.toFloat()
        val windowHeight = mc.window.height.toFloat()

        val realX = rect.x() * scale
        val realY = rect.y() * scale
        val realW = rect.width() * scale
        val realH = rect.height() * scale

        val glMinY = windowHeight - (realY + realH)
        val glMaxY = windowHeight - realY
        val glMinX = realX
        val glMaxX = realX + realW

        return CACHE_SCISSOR.set(glMinX, glMinY, glMaxX, glMaxY)
    }
}
