package com.sixeyes.module.setting

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

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

    override fun serialize(): JsonElement = JsonPrimitive(value)

    override fun deserialize(element: JsonElement) {
        if (element.isJsonPrimitive && element.asJsonPrimitive.isString) {
            val str = element.asString
            if (modes.any { it.equals(str, ignoreCase = true) }) {
                set(str)
            }
        }
    }
}
