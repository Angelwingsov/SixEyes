package com.sixeyes.client.command.impl

import com.sixeyes.client.command.Command
import com.sixeyes.client.extensions.safeRun
import com.sixeyes.client.util.other.ChatUtil

object VClipCommand : Command(
    name = "vclip",
    description = "Teleport vertically by a specified number of blocks",
    syntax = "vclip <offset>",
    aliases = listOf("vc")
) {
    override fun execute(args: Array<String>) {
        if (args.isEmpty()) {
            sendUsage()
            return
        }

        val offset = args[0].toDoubleOrNull()
        if (offset == null) {
            ChatUtil.error("Invalid offset number '${args[0]}'")
            return
        }

        safeRun {
            player.setPos(player.x, player.y + offset, player.z)
            val sign = if (offset >= 0) "+$offset" else "$offset"
            ChatUtil.success("Teleported $sign blocks vertically")
        } ?: run {
            ChatUtil.error("Cannot execute vclip: not in game!")
        }
    }
}
