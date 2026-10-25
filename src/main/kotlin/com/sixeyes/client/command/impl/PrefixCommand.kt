package com.sixeyes.client.command.impl

import com.sixeyes.client.command.Command
import com.sixeyes.client.command.CommandManager
import com.sixeyes.client.config.ConfigManager
import com.sixeyes.client.util.other.ChatUtil

object PrefixCommand : Command(
    name = "prefix",
    description = "Change the client command prefix",
    syntax = "prefix <symbol>",
    aliases = listOf("p")
) {
    override fun execute(args: Array<String>) {
        if (args.isEmpty()) {
            ChatUtil.send("Current command prefix is '§d${CommandManager.prefix}§f'")
            return
        }

        val newPrefix = args[0].trim()
        if (newPrefix.isEmpty() || newPrefix.length > 2 || newPrefix == "/") {
            ChatUtil.error("Invalid prefix '$newPrefix'! Prefix cannot be empty, longer than 2 characters, or '/'")
            return
        }

        val oldPrefix = CommandManager.prefix
        CommandManager.prefix = newPrefix
        ConfigManager.saveConfig("default")
        ChatUtil.success("Command prefix changed from '§7$oldPrefix§a' to '§d$newPrefix§a'")
    }
}
