package com.sixeyes.module.modules.render

import com.mojang.blaze3d.platform.InputConstants
import com.sixeyes.client.extensions.RENDER
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.gui.material.ClickGuiScreen
import com.sixeyes.module.Module

object ClickGuiModule : Module(
    name = "ClickGUI",
    category = RENDER,
    description = "Material 3 DropDown Graphical User Interface",
    key = InputConstants.KEY_RSHIFT
) {
    val blur = boolean(
        name = "Background Scrim",
        value = true,
        description = "Dim background behind the GUI"
    )

    override fun onEnable() {
        if (mc.gui.screen() == null) {
            mc.gui.setScreen(ClickGuiScreen)
        }
    }

    override fun onDisable() {
        if (mc.gui.screen() is ClickGuiScreen) {
            mc.gui.setScreen(null)
        }
    }
}
