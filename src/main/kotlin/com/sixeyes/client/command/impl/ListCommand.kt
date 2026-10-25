package com.sixeyes.client.command.impl

import com.sixeyes.client.command.Command
import com.sixeyes.client.extensions.Category
import com.sixeyes.client.util.other.ChatUtil
import com.sixeyes.module.ModuleManager

object ListCommand : Command(
    name = "list",
    description = "List all client modules by category",
    syntax = "list [category]",
    aliases = listOf("modules", "mods")
) {
    override fun execute(args: Array<String>) {
        if (args.isNotEmpty()) {
            val catName = args[0]
            val category = Category.entries.firstOrNull {
                it.displayName.equals(catName, ignoreCase = true) || it.name.equals(catName, ignoreCase = true)
            }
            if (category == null) {
                ChatUtil.error("Unknown category '$catName'. Available: ${Category.entries.joinToString { it.displayName }}")
                return
            }

            val mods = ModuleManager.getByCategory(category)
            ChatUtil.send("${category.displayName} Modules (${mods.size}):")
            val formatted = mods.joinToString("§7, ") {
                if (it.isEnabled()) "§a${it.name}" else "§7${it.name}"
            }
            ChatUtil.raw("  $formatted")
            return
        }

        var enabledCount = 0
        var totalCount = 0

        ChatUtil.send("SixEyes Modules:")
        for (category in Category.entries) {
            val mods = ModuleManager.getByCategory(category)
            if (mods.isEmpty()) continue
            totalCount += mods.size
            enabledCount += mods.count { it.isEnabled() }

            val formatted = mods.joinToString("§7, ") {
                if (it.isEnabled()) "§a${it.name}" else "§7${it.name}"
            }
            ChatUtil.raw("  §d${category.displayName}§8: $formatted")
        }

        val disabledCount = totalCount - enabledCount
        ChatUtil.raw("  §7Total: §f$totalCount §7(§a$enabledCount enabled§7, §c$disabledCount disabled§7)")
    }
}
