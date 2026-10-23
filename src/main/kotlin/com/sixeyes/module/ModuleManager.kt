package com.sixeyes.module

import com.sixeyes.client.extensions.Category
import com.sixeyes.client.interfaces.ILoadable
import com.sixeyes.module.modules.movement.InventoryMoveModule
import com.sixeyes.module.modules.other.TestModule
import com.sixeyes.module.modules.render.ClickGuiModule

object ModuleManager : ILoadable {
    val modules: MutableList<Module> = ArrayList()

    override fun load() {
        modules.clear()
        add(
            TestModule,
            ClickGuiModule,
            InventoryMoveModule
        )
    }

    private fun add(vararg module: Module) {
        modules.addAll(module)
    }

    fun getByCategory(category: Category): List<Module> =
        modules.filter { it.category == category }

    fun getByName(name: String): Module? =
        modules.firstOrNull { it.name.equals(name, ignoreCase = true) }

    inline fun <reified T : Module> get(): T? =
        modules.firstOrNull { it is T } as? T
}
