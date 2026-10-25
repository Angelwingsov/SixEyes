package com.sixeyes.client.command.impl

import com.sixeyes.client.command.Command
import com.sixeyes.client.command.CommandManager
import com.sixeyes.client.config.ConfigManager
import com.sixeyes.client.util.other.ChatUtil

object PresetCommand : Command(
    name = "preset",
    description = "Manage configuration presets",
    syntax = "preset <load <name> | save <name> | delete <name> | clear | list | dir>",
    aliases = listOf("cfg", "config")
) {
    override fun execute(args: Array<String>) {
        val p = CommandManager.prefix

        if (args.isEmpty()) {
            sendUsage()
            return
        }

        when (args[0].lowercase()) {
            "load" -> {
                if (args.size < 2) {
                    ChatUtil.error("Usage: ${p}preset load <name>")
                    return
                }
                val name = args[1]
                if (ConfigManager.loadConfig(name)) {
                    ChatUtil.success("Successfully loaded preset '$name'")
                } else {
                    ChatUtil.error("Preset '$name' does not exist or failed to load!")
                }
            }

            "save", "create" -> {
                if (args.size < 2) {
                    ChatUtil.error("Usage: ${p}preset save <name>")
                    return
                }
                val name = args[1]
                if (ConfigManager.saveConfig(name)) {
                    ChatUtil.success("Successfully saved preset '$name'")
                } else {
                    ChatUtil.error("Failed to save preset '$name'!")
                }
            }

            "delete", "del", "remove" -> {
                if (args.size < 2) {
                    ChatUtil.error("Usage: ${p}preset delete <name>")
                    return
                }
                val name = args[1]
                if (ConfigManager.deleteConfig(name)) {
                    ChatUtil.success("Deleted preset '$name'")
                } else {
                    ChatUtil.error("Preset '$name' does not exist!")
                }
            }

            "clear" -> {
                val count = ConfigManager.clearConfigs()
                ChatUtil.success("Cleared all presets ($count deleted)")
            }

            "list" -> {
                val configs = ConfigManager.listConfigs()
                if (configs.isEmpty()) {
                    ChatUtil.send("No presets found in directory.")
                    return
                }
                ChatUtil.send("Available presets (${configs.size}):")
                ChatUtil.raw("  §7" + configs.joinToString("§7, §f", prefix = "§f"))
            }

            "dir", "folder", "open" -> {
                if (ConfigManager.openDirectory()) {
                    ChatUtil.success("Opened presets directory")
                } else {
                    ChatUtil.error("Failed to open presets directory")
                }
            }

            else -> {
                ChatUtil.error("Unknown subcommand '${args[0]}'.")
                sendUsage()
            }
        }
    }
}
