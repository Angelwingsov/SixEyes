package com.sixeyes.client.gui.material.components

import com.sixeyes.module.setting.*

object SettingComponentFactory {
    fun create(setting: Setting<*>): SettingComponent = when (setting) {
        is BooleanSetting -> BooleanComponent(setting)
        is NumberSetting -> NumberComponent(setting)
        is EnumSetting<*> -> ModeComponent(setting)
        is ModeSetting -> ModeComponent(setting)
        is MultiModeSetting -> ModeComponent(setting)
        is ColorSetting -> ColorComponent(setting)
        is KeybindSetting -> KeybindComponent(setting)
    }
}
