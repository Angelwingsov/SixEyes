package com.sixeyes.module.setting

import org.lwjgl.glfw.GLFW

class KeybindSetting(
    name: String,
    defaultValue: Int = GLFW.GLFW_KEY_UNKNOWN,
    description: String = "",
    isVisible: () -> Boolean = { true }
) : Setting<Int>(name, defaultValue, defaultValue, description, isVisible) {

    fun getKeyName(): String {
        return formatKey(value)
    }

    companion object {
        fun formatKey(key: Int): String {
            if (key <= 0 || key == GLFW.GLFW_KEY_UNKNOWN) return "NONE"
            return when (key) {
                GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT"
                GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT"
                GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCTRL"
                GLFW.GLFW_KEY_LEFT_CONTROL -> "LCTRL"
                GLFW.GLFW_KEY_RIGHT_ALT -> "RALT"
                GLFW.GLFW_KEY_LEFT_ALT -> "LALT"
                GLFW.GLFW_KEY_SPACE -> "SPACE"
                GLFW.GLFW_KEY_TAB -> "TAB"
                GLFW.GLFW_KEY_ENTER -> "ENTER"
                GLFW.GLFW_KEY_BACKSPACE -> "BACK"
                GLFW.GLFW_KEY_DELETE -> "DEL"
                GLFW.GLFW_KEY_ESCAPE -> "ESC"
                GLFW.GLFW_KEY_GRAVE_ACCENT -> "`"
                GLFW.GLFW_KEY_CAPS_LOCK -> "CAPS"
                GLFW.GLFW_KEY_UP -> "UP"
                GLFW.GLFW_KEY_DOWN -> "DOWN"
                GLFW.GLFW_KEY_LEFT -> "LEFT"
                GLFW.GLFW_KEY_RIGHT -> "RIGHT"
                else -> {
                    val name = GLFW.glfwGetKeyName(key, 0)
                    name?.uppercase() ?: "KEY_$key"
                }
            }
        }
    }
}
