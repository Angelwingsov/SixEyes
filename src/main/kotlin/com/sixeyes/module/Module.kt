package com.sixeyes.module

import com.sixeyes.client.extensions.Category
import com.sixeyes.client.interfaces.IToggable
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.event.EventManager
import com.sixeyes.module.setting.*
import org.lwjgl.glfw.GLFW

open class Module(
    val name: String,
    val category: Category,
    val description: String = "",
    var key: Int = GLFW.GLFW_KEY_UNKNOWN
) : IToggable {
    private var enabled = false
    var isExpanded = false
    val settings: MutableList<Setting<*>> = ArrayList()

    override fun isEnabled(): Boolean {
        return enabled
    }

    override fun setEnabled(value: Boolean) {
        if (value == enabled) return

        enabled = value

        if (value) {
            superEnable()
            onEnable()
        } else {
            superDisable()
            onDisable()
        }
    }

    override fun toggle() {
        setEnabled(!enabled)
    }

    private fun superDisable() {
        EventManager.unregister(this)
    }

    private fun superEnable() {
        EventManager.register(this)
    }

    open fun onDisable() {}
    open fun onEnable() {}

    fun <T : Setting<*>> register(setting: T): T {
        settings.add(setting)
        return setting
    }

    fun boolean(name: String, value: Boolean, description: String = "", isVisible: () -> Boolean = { true }) =
        register(BooleanSetting(name, value, description, isVisible))

    fun number(name: String, value: Double, min: Double, max: Double, step: Double = 0.1, precision: Int = 1, unit: String = "", description: String = "", isVisible: () -> Boolean = { true }) =
        register(NumberSetting(name, value, min, max, step, precision, unit, description, isVisible))

    fun mode(name: String, value: String, modes: List<String>, description: String = "", isVisible: () -> Boolean = { true }) =
        register(ModeSetting(name, value, modes, description, isVisible))

    fun multiMode(name: String, value: Set<String>, modes: List<String>, description: String = "", isVisible: () -> Boolean = { true }) =
        register(MultiModeSetting(name, value, modes, description, isVisible))

    fun color(name: String, value: Color4b, hasAlpha: Boolean = true, description: String = "", isVisible: () -> Boolean = { true }) =
        register(ColorSetting(name, value, hasAlpha, description, isVisible))

    fun keybind(name: String, value: Int = GLFW.GLFW_KEY_UNKNOWN, description: String = "", isVisible: () -> Boolean = { true }) =
        register(KeybindSetting(name, value, description, isVisible))
}