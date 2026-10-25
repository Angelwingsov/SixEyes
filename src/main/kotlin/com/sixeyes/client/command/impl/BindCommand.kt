package com.sixeyes.client.command.impl

import com.mojang.blaze3d.platform.InputConstants
import com.sixeyes.client.command.Command
import com.sixeyes.client.command.CommandManager
import com.sixeyes.client.config.ConfigManager
import com.sixeyes.client.util.other.ChatUtil
import com.sixeyes.module.ModuleManager
import com.sixeyes.module.setting.KeybindSetting

object BindCommand : Command(
    name = "bind",
    description = "Manage module keybinds",
    syntax = "bind <add <module> <key> | remove <module> | clear | list>",
    aliases = listOf("b", "keybind")
) {
    override fun execute(args: Array<String>) {
        if (args.isEmpty()) {
            sendUsage()
            return
        }

        when (args[0].lowercase()) {
            "add", "set" -> {
                if (args.size < 3) {
                    ChatUtil.error("Usage: ${CommandManager.prefix}bind add <module> <key>")
                    return
                }
                val moduleName = args[1]
                val module = ModuleManager.getByName(moduleName)
                if (module == null) {
                    ChatUtil.error("Module '$moduleName' not found!")
                    return
                }

                val keyName = args[2]
                val key = KeybindSetting.parseKey(keyName)
                if (key == InputConstants.UNKNOWN.value && !keyName.equals("none", ignoreCase = true)) {
                    ChatUtil.error("Invalid key '$keyName'!")
                    return
                }

                module.key = key
                ConfigManager.saveConfig("default")
                val formatted = KeybindSetting.formatKey(key)
                ChatUtil.success("Bound ${module.name} to $formatted")
            }

            "remove", "del", "delete" -> {
                if (args.size < 2) {
                    ChatUtil.error("Usage: ${CommandManager.prefix}bind remove <module>")
                    return
                }
                val moduleName = args[1]
                val module = ModuleManager.getByName(moduleName)
                if (module == null) {
                    ChatUtil.error("Module '$moduleName' not found!")
                    return
                }

                module.key = InputConstants.UNKNOWN.value
                ConfigManager.saveConfig("default")
                ChatUtil.success("Removed keybind for ${module.name}")
            }

            "clear" -> {
                var cleared = 0
                for (module in ModuleManager.modules) {
                    if (module.key != InputConstants.UNKNOWN.value) {
                        module.key = InputConstants.UNKNOWN.value
                        cleared++
                    }
                }
                ConfigManager.saveConfig("default")
                ChatUtil.success("Cleared $cleared keybinds")
            }

            "list" -> {
                val bound = ModuleManager.modules.filter { it.key != InputConstants.UNKNOWN.value }
                if (bound.isEmpty()) {
                    ChatUtil.send("No modules are currently bound.")
                    return
                }

                ChatUtil.send("Active Keybinds (${bound.size}):")
                for (mod in bound) {
                    ChatUtil.send("§7- §f${mod.name} §7[§d${KeybindSetting.formatKey(mod.key)}§7]")
                }
            }

            else -> {
                ChatUtil.error("Unknown subcommand '${args[0]}'. Usage: ${CommandManager.prefix}$syntax")
            }
        }
    }
}
