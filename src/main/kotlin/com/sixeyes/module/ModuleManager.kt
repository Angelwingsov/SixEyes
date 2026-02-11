package com.sixeyes.module

import com.sixeyes.client.interfaces.ILoadable
import com.sixeyes.module.modules.other.TestModule

object ModuleManager : ILoadable {
    val modules: MutableList<Module> = ArrayList()

    override fun load() {
        add(
            TestModule
        )
    }

    private fun add(vararg module: Module) {
        modules.addAll(module)
    }
}