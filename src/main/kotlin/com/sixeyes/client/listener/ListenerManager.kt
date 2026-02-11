package com.sixeyes.client.listener

import com.sixeyes.client.interfaces.ILoadable

object ListenerManager : ILoadable {
    val listeners: MutableList<Listener> = ArrayList()

    override fun load() {
        add(

        )
    }

    private fun add(vararg listener: Listener) {
        listeners.addAll(listener)
    }
}