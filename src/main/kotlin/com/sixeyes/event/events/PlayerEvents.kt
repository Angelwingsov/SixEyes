package com.sixeyes.event.events

import com.sixeyes.event.types.Event

class PlayerUpdateEvent : Event() {
    companion object {
        @JvmField
        val INSTANCE = PlayerUpdateEvent()
    }
}
