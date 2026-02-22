package com.sixeyes.module

import com.sixeyes.client.interfaces.ILoadable
import com.sixeyes.module.modules.render.TestRenderModule

object ModuleManager : ILoadable {
    val modules: MutableList<Module> = ArrayList()

    override fun load() {
        add(
            // Combat

            // Movement

            // Player

            // Other

            // Render
            TestRenderModule
        )
    }

    private fun add(vararg module: Module) {
        modules.addAll(module)
    }
}