package com.sixeyes.client.listener

import com.sixeyes.client.interfaces.ILoadable
import com.sixeyes.client.listener.listeners.InputListener
import com.sixeyes.client.listener.listeners.RenderListener

object ListenerManager : ILoadable {
    val listeners: MutableList<Listener> = ArrayList()

    override fun load() {
        listeners.clear()
        add(
            InputListener,
            RenderListener
        )
        listeners.forEach(Listener::init)
    }

    private fun add(vararg listener: Listener) {
        listeners.addAll(listener)
    }
}
