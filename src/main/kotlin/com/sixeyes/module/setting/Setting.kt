package com.sixeyes.module.setting

abstract class Setting<T>(
    val name: String,
    var value: T,
    val defaultValue: T,
    val description: String = "",
    val isVisible: () -> Boolean = { true }
) {
    private val listeners = mutableListOf<(T) -> Unit>()

    fun addListener(listener: (T) -> Unit): Setting<T> {
        listeners.add(listener)
        return this
    }

    open fun set(newValue: T) {
        if (value != newValue) {
            value = newValue
            listeners.forEach { it(newValue) }
        }
    }

    fun reset() {
        set(defaultValue)
    }
}
