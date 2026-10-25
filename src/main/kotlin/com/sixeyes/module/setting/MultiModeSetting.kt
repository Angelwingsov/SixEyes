package com.sixeyes.module.setting

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

class MultiModeSetting(
    name: String,
    defaultValue: Set<String>,
    val modes: List<String>,
    description: String = "",
    isVisible: () -> Boolean = { true }
) : Setting<Set<String>>(name, defaultValue, defaultValue, description, isVisible) {

    fun isSelected(m: String): Boolean = value.contains(m)

    fun toggle(m: String) {
        val newSet = value.toMutableSet()
        if (newSet.contains(m)) {
            newSet.remove(m)
        } else {
            newSet.add(m)
        }
        set(newSet)
    }

    override fun serialize(): JsonElement {
        val array = JsonArray()
        value.forEach { array.add(JsonPrimitive(it)) }
        return array
    }

    override fun deserialize(element: JsonElement) {
        if (element.isJsonArray) {
            val newSet = mutableSetOf<String>()
            element.asJsonArray.forEach {
                if (it.isJsonPrimitive && it.asJsonPrimitive.isString) {
                    newSet.add(it.asString)
                }
            }
            set(newSet)
        }
    }
}
