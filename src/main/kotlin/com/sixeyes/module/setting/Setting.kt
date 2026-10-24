package com.sixeyes.module.setting

import com.sixeyes.module.Module
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class Setting<T>(
    val name: String,
    var value: T,
    val defaultValue: T,
    val description: String = "",
    val isVisible: () -> Boolean = { true }
) : ReadWriteProperty<Any?, T> {
    private val listeners = mutableListOf<(T) -> Unit>()

    operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): Setting<T> {
        if (thisRef is Module && !thisRef.settings.contains(this)) {
            thisRef.settings.add(this)
        }
        return this
    }

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

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        set(value)
    }
}
