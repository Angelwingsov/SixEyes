package com.sixeyes.client.command.impl

import com.sixeyes.client.command.Command
import com.sixeyes.client.config.ConfigManager
import com.sixeyes.client.util.other.ChatUtil
import com.sixeyes.module.ModuleManager

object ToggleCommand : Command(
    name = "toggle",
    description = "Toggle a module on or off",
    syntax = "toggle <module> [on | off]",
    aliases = listOf("t")
) {
    override fun execute(args: Array<String>) {
        if (args.isEmpty()) {
            sendUsage()
            return
        }

        val moduleName = args[0]
        val module = ModuleManager.getByName(moduleName)
        if (module == null) {
            ChatUtil.error("Module '$moduleName' not found!")
            return
        }

        val newState = if (args.size > 1) {
            when (args[1].lowercase()) {
                "on", "true", "1", "enable" -> true
                "off", "false", "0", "disable" -> false
                else -> !module.isEnabled()
            }
        } else {
            !module.isEnabled()
        }

        module.setEnabled(newState)
        ConfigManager.saveConfig("default")

        val stateTag = if (module.isEnabled()) "§aENABLED" else "§cDISABLED"
        ChatUtil.send("${module.name} is now $stateTag§f")
    }
}
