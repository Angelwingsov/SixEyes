package com.sixeyes.module.setting

import com.sixeyes.client.util.other.color.Color4b

class ColorSetting(
    name: String,
    defaultValue: Color4b,
    val hasAlpha: Boolean = true,
    description: String = "",
    isVisible: () -> Boolean = { true }
) : Setting<Color4b>(name, defaultValue, defaultValue, description, isVisible)
