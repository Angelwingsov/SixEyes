package com.sixeyes.event.events

import com.sixeyes.event.types.Event

class OverlayRenderEvent : Event() {
    companion object {
        val PARTIAL_TICKS = Key<Float>()
        val MOUSE_X = Key<Int>()
        val MOUSE_Y = Key<Int>()
    }
}
