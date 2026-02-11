package com.sixeyes.module

import com.sixeyes.client.extensions.Category
import com.sixeyes.client.interfaces.IToggable
import com.sixeyes.event.EventManager

open class Module(
    val name: String,
    val category: Category
) : IToggable {
    private var enabled = false

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

    fun onDisable() {}
    fun onEnable() {}
}