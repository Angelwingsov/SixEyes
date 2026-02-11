package com.sixeyes.client.listener.listeners

import com.sixeyes.client.extensions.mc
import com.sixeyes.client.listener.Listener

object InputListener : Listener() {
    override fun init() {

    }

    fun mouseX(): Int {
        return (mc.mouseHandler.xpos() / mc.window.guiScale).toInt()
    }

    fun mouseY(): Int {
        return (mc.mouseHandler.ypos() / mc.window.guiScale).toInt()
    }
}