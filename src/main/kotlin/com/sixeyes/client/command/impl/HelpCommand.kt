package com.sixeyes.client.command.impl

import com.sixeyes.client.command.Command
import com.sixeyes.client.command.CommandManager
import com.sixeyes.client.util.other.ChatUtil

object HelpCommand : Command(
    name = "help",
    description = "Displays available commands or details about a command",
    syntax = "help [command]",
    aliases = listOf("?", "??", "commands", "cmds")
) {
    override fun execute(args: Array<String>) {
        val p = CommandManager.prefix

        if (args.isEmpty()) {
            val list = CommandManager.commands.sortedBy { it.name }
            ChatUtil.send("Available Commands (§d${list.size}§f):")
            for (cmd in list) {
                val usageSnippet = if (cmd.syntax.startsWith(cmd.name, ignoreCase = true)) {
                    cmd.syntax.substring(cmd.name.length).trim()
                } else {
                    cmd.syntax
                }
                val usageFormatted = if (usageSnippet.isNotEmpty()) " §7$usageSnippet" else ""
                ChatUtil.raw("  §d$p${cmd.name}$usageFormatted §8- §f${cmd.description}")
            }
            ChatUtil.raw("  §7Type §d${p}help <command>§7 or §d$p? <command>§7 for details.")
            return
        }

        val targetName = args[0]
        val cmd = CommandManager.getCommand(targetName)
        if (cmd == null) {
            ChatUtil.error("Command '$targetName' not found! Type '${p}help' or '$p?' for all commands.")
            return
        }

        ChatUtil.send("Command Details: §d${cmd.name}")
        ChatUtil.raw("  §7Description: §f${cmd.description}")
        ChatUtil.raw("  §7Syntax: §d$p${cmd.syntax}")
        val aliasText = if (cmd.aliases.isEmpty()) "None" else cmd.aliases.joinToString(", ") { "$p$it" }
        ChatUtil.raw("  §7Aliases: §f$aliasText")
    }
}
