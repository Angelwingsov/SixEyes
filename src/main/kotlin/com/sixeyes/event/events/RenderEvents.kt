package com.sixeyes.event.events

import com.sixeyes.event.types.Event

class OverlayRenderEvent(
    var partialTicks: Float = 0f,
    var mouseX: Int = 0,
    var mouseY: Int = 0
) : Event() {
    fun update(partialTicks: Float, mouseX: Int, mouseY: Int): OverlayRenderEvent {
        this.partialTicks = partialTicks
        this.mouseX = mouseX
        this.mouseY = mouseY
        return this
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(key: Key<T>): T? = when (key) {
        PARTIAL_TICKS -> partialTicks as T
        MOUSE_X -> mouseX as T
        MOUSE_Y -> mouseY as T
        else -> super.get(key)
    }

    companion object {
        val PARTIAL_TICKS = Key<Float>()
        val MOUSE_X = Key<Int>()
        val MOUSE_Y = Key<Int>()
    }
}
