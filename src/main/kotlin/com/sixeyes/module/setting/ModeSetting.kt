package com.sixeyes.module.setting

class ModeSetting(
    name: String,
    defaultValue: String,
    val modes: List<String>,
    description: String = "",
    isVisible: () -> Boolean = { true }
) : Setting<String>(name, defaultValue, defaultValue, description, isVisible) {

    fun isMode(m: String): Boolean = value.equals(m, ignoreCase = true)

    fun next() {
        if (modes.isEmpty()) return
        val idx = modes.indexOf(value)
        set(modes[(idx + 1) % modes.size])
    }

    fun prev() {
        if (modes.isEmpty()) return
        val idx = modes.indexOf(value)
        set(modes[(idx - 1 + modes.size) % modes.size])
    }
}
