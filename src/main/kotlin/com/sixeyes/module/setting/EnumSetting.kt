package com.sixeyes.module.setting

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

class EnumSetting<E : Enum<E>>(
    name: String,
    defaultValue: E,
    val entries: Array<E>,
    description: String = "",
    val nameMapper: (E) -> String = { it.name },
    isVisible: () -> Boolean = { true }
) : Setting<E>(name, defaultValue, defaultValue, description, isVisible) {

    val modes: List<String> get() = entries.map(nameMapper)

    fun isMode(entry: E): Boolean = value == entry

    fun isMode(name: String): Boolean =
        nameMapper(value).equals(name, ignoreCase = true) || value.name.equals(name, ignoreCase = true)

    fun selectByName(name: String) {
        val found = entries.firstOrNull {
            nameMapper(it).equals(name, ignoreCase = true) || it.name.equals(name, ignoreCase = true)
        }
        if (found != null) {
            set(found)
        }
    }

    fun next() {
        if (entries.isEmpty()) return
        val idx = entries.indexOf(value)
        set(entries[(idx + 1) % entries.size])
    }

    fun prev() {
        if (entries.isEmpty()) return
        val idx = entries.indexOf(value)
        set(entries[(idx - 1 + entries.size) % entries.size])
    }

    override fun serialize(): JsonElement = JsonPrimitive(value.name)

    override fun deserialize(element: JsonElement) {
        if (element.isJsonPrimitive && element.asJsonPrimitive.isString) {
            selectByName(element.asString)
        }
    }
}
