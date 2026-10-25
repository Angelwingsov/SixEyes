package com.sixeyes.module.setting

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

class BooleanSetting(
    name: String,
    defaultValue: Boolean,
    description: String = "",
    isVisible: () -> Boolean = { true }
) : Setting<Boolean>(name, defaultValue, defaultValue, description, isVisible) {

    fun toggle() {
        set(!value)
    }

    override fun serialize(): JsonElement = JsonPrimitive(value)

    override fun deserialize(element: JsonElement) {
        if (element.isJsonPrimitive && element.asJsonPrimitive.isBoolean) {
            set(element.asBoolean)
        }
    }
}
