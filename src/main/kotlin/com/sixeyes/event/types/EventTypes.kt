package com.sixeyes.event.types

open class EventCancellable : Event() {
    var cancel: Boolean = false

    fun cancel() {
        cancel = true
    }
}

abstract class Event {
    private var data: HashMap<Key<*>, Any>? = null

    open fun <T : Any> put(key: Key<T>, value: T) {
        val map = data ?: HashMap<Key<*>, Any>(4).also { data = it }
        map[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    open fun <T : Any> get(key: Key<T>): T? {
        return data?.get(key) as? T
    }

    class Key<T : Any>
}
