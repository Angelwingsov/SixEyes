package com.sixeyes.event.types

open class EventCancellable : Event() {
    var cancel: Boolean = false
}

abstract class Event {
    private val data = HashMap<Key<*>, Any>()

    fun <T : Any> put(key: Key<T>, value: T) {
        data[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(key: Key<T>): T? {
        return data[key] as? T
    }

    class Key<T : Any>
}