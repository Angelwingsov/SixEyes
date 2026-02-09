package com.sixeyes.module

import com.sixeyes.client.interfaces.Loadable
import com.sixeyes.module.modules.TestModule

object ModuleManager : Loadable {
    val modules: MutableList<Module> = ArrayList()

    override fun load() {
        add(
            TestModule
        )
    }

    private fun add(vararg module: Module) {
        modules.addAll(modules)
    }
}