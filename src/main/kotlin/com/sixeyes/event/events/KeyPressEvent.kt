package com.sixeyes.event.events

import com.sixeyes.event.types.EventCancellable

class KeyPressEvent(
    val key: Int,
    val scancode: Int,
    val action: Int,
    val modifiers: Int
) : EventCancellable()
