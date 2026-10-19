package com.sixeyes.module.setting

class BooleanSetting(
    name: String,
    defaultValue: Boolean,
    description: String = "",
    isVisible: () -> Boolean = { true }
) : Setting<Boolean>(name, defaultValue, defaultValue, description, isVisible) {
    fun toggle() {
        set(!value)
    }
}
