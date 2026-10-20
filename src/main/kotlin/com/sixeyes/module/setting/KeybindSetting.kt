package com.sixeyes.module.setting

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
    }
}
