package com.sixeyes.module.modules.render

import com.mojang.blaze3d.platform.InputConstants
import com.sixeyes.client.extensions.RENDER
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.gui.material.ClickGuiScreen
import com.sixeyes.module.Module
import net.minecraft.client.gui.screens.TitleScreen

object ClickGuiModule : Module(
    name = "ClickGUI",
    category = RENDER,
    description = "Material 3 DropDown Graphical User Interface",
    key = InputConstants.KEY_RSHIFT
) {
    val blur by boolean(
        name = "Background Scrim",
        value = true,
        description = "Dim background behind the GUI"
    )

    override fun onEnable() {
        val screen = mc.gui.screen()
        if (screen == null || screen is TitleScreen) {
            ClickGuiScreen.parentScreen = screen
            mc.gui.setScreen(ClickGuiScreen)
        } else {
            setEnabled(false)
        }
    }

    override fun onDisable() {
        if (mc.gui.screen() is ClickGuiScreen) {
            val parent = ClickGuiScreen.parentScreen
            ClickGuiScreen.parentScreen = null
            mc.gui.setScreen(parent)
        }
    }
}
