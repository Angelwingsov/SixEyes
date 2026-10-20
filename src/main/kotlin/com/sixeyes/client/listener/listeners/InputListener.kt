package com.sixeyes.client.listener.listeners

import com.mojang.blaze3d.platform.InputConstants
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.listener.Listener
import com.sixeyes.event.EventManager
import com.sixeyes.event.events.KeyPressEvent
import com.sixeyes.module.ModuleManager
import com.sixeyes.module.modules.render.ClickGuiModule
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
        if (event.action == InputConstants.PRESS) {
            if (mc.gui.screen() == null) {
                if (event.key == ClickGuiModule.key) {
                    ClickGuiModule.toggle()
                    event.cancel = true
                    return
                }

                for (module in ModuleManager.modules) {
                    if (module.key != InputConstants.UNKNOWN.value && module.key == event.key) {
                        module.toggle()
                    }
                }
            }
        }
    }
}