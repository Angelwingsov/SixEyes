package com.sixeyes.client.listener.listeners

import com.sixeyes.client.extensions.mc
import com.sixeyes.client.listener.Listener
import com.sixeyes.event.EventManager
import com.sixeyes.event.events.KeyPressEvent
import com.sixeyes.module.ModuleManager
import com.sixeyes.module.modules.render.ClickGuiModule
import org.lwjgl.glfw.GLFW
import sweetie.evaware.flora.api.Commando

object InputListener : Listener() {
    override fun init() {
        EventManager.register(this)
    }

    fun mouseX(): Int {
        return (mc.mouseHandler.xpos() / mc.window.guiScale).toInt()
    }

    fun mouseY(): Int {
        return (mc.mouseHandler.ypos() / mc.window.guiScale).toInt()
    }

    @Commando
    fun onKeyPress(event: KeyPressEvent) {
        if (event.action == GLFW.GLFW_PRESS) {
            if (mc.screen == null) {
                if (event.key == ClickGuiModule.key) {
                    ClickGuiModule.toggle()
                    event.cancel = true
                    return
                }

                for (module in ModuleManager.modules) {
                    if (module.key != GLFW.GLFW_KEY_UNKNOWN && module.key == event.key) {
                        module.toggle()
                    }
                }
            }
        }
    }
}