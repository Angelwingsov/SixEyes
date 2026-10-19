package com.sixeyes.module.modules.render

import com.sixeyes.client.extensions.RENDER
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.gui.material.ClickGuiScreen
import com.sixeyes.module.Module
import org.lwjgl.glfw.GLFW

object ClickGuiModule : Module(
    name = "ClickGUI",
    category = RENDER,
    description = "Material 3 DropDown Graphical User Interface",
    key = GLFW.GLFW_KEY_RIGHT_SHIFT
) {
    val blur = boolean(
        name = "Background Scrim",
        value = true,
        description = "Dim background behind the GUI"
    )

    override fun onEnable() {
        if (mc.screen == null) {
            mc.setScreen(ClickGuiScreen)
        }
    }

    override fun onDisable() {
        if (mc.screen is ClickGuiScreen) {
            mc.setScreen(null)
        }
    }
}
