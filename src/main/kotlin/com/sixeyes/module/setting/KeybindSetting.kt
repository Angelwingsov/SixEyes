package com.sixeyes.module.setting

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.mojang.blaze3d.platform.InputConstants

class KeybindSetting(
    name: String,
    defaultValue: Int = InputConstants.UNKNOWN.value,
    description: String = "",
    isVisible: () -> Boolean = { true }
) : Setting<Int>(name, defaultValue, defaultValue, description, isVisible) {

    fun getKeyName(): String {
        return formatKey(value)
    }

    override fun serialize(): JsonElement = JsonPrimitive(value)

    override fun deserialize(element: JsonElement) {
        if (element.isJsonPrimitive && element.asJsonPrimitive.isNumber) {
            set(element.asInt)
        }
    }

    companion object {
        fun formatKey(key: Int): String {
            if (key <= 0 || key == InputConstants.UNKNOWN.value) return "NONE"
            return when (key) {
                InputConstants.KEY_RSHIFT -> "RSHIFT"
                InputConstants.KEY_LSHIFT -> "LSHIFT"
                InputConstants.KEY_RCONTROL -> "RCTRL"
                InputConstants.KEY_LCONTROL -> "LCTRL"
                InputConstants.KEY_RALT -> "RALT"
                InputConstants.KEY_LALT -> "LALT"
                InputConstants.KEY_SPACE -> "SPACE"
                InputConstants.KEY_TAB -> "TAB"
                InputConstants.KEY_RETURN -> "ENTER"
                InputConstants.KEY_BACKSPACE -> "BACK"
                InputConstants.KEY_DELETE -> "DEL"
                InputConstants.KEY_ESCAPE -> "ESC"
                InputConstants.KEY_GRAVE -> "`"
                InputConstants.KEY_CAPSLOCK -> "CAPS"
                InputConstants.KEY_UP -> "UP"
                InputConstants.KEY_DOWN -> "DOWN"
                InputConstants.KEY_LEFT -> "LEFT"
                InputConstants.KEY_RIGHT -> "RIGHT"
                else -> {
                    try {
                        InputConstants.Type.KEYBOARD.getOrCreate(key).displayName.string.uppercase()
                    } catch (_: Exception) {
                        "KEY_$key"
                    }
                }
            }
        }

        fun parseKey(input: String): Int {
            val trimmed = input.trim().uppercase()
            if (trimmed.isEmpty() || trimmed == "NONE" || trimmed == "CLEAR" || trimmed == "0") {
                return InputConstants.UNKNOWN.value
            }
            return when (trimmed) {
                "RSHIFT", "RIGHT_SHIFT" -> InputConstants.KEY_RSHIFT
                "LSHIFT", "LEFT_SHIFT", "SHIFT" -> InputConstants.KEY_LSHIFT
                "RCTRL", "RCONTROL", "RIGHT_CTRL" -> InputConstants.KEY_RCONTROL
                "LCTRL", "LCONTROL", "LEFT_CTRL", "CTRL" -> InputConstants.KEY_LCONTROL
                "RALT", "RIGHT_ALT" -> InputConstants.KEY_RALT
                "LALT", "LEFT_ALT", "ALT" -> InputConstants.KEY_LALT
                "SPACE" -> InputConstants.KEY_SPACE
                "TAB" -> InputConstants.KEY_TAB
                "ENTER", "RETURN" -> InputConstants.KEY_RETURN
                "BACK", "BACKSPACE" -> InputConstants.KEY_BACKSPACE
                "DEL", "DELETE" -> InputConstants.KEY_DELETE
                "ESC", "ESCAPE" -> InputConstants.KEY_ESCAPE
                "GRAVE", "`", "~" -> InputConstants.KEY_GRAVE
                "CAPS", "CAPSLOCK" -> InputConstants.KEY_CAPSLOCK
                "UP" -> InputConstants.KEY_UP
                "DOWN" -> InputConstants.KEY_DOWN
                "LEFT" -> InputConstants.KEY_LEFT
                "RIGHT" -> InputConstants.KEY_RIGHT
                else -> {
                    try {
                        InputConstants.getKey("key.keyboard." + trimmed.lowercase()).value
                    } catch (_: Exception) {
                        try {
                            trimmed.toInt()
                        } catch (_: Exception) {
                            InputConstants.UNKNOWN.value
                        }
                    }
                }
            }
        }
    }
}
