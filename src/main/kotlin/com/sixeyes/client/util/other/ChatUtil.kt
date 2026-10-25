package com.sixeyes.client.util.other

import com.sixeyes.client.extensions.mc
import net.minecraft.network.chat.Component

object ChatUtil {
    fun send(message: String) {
        mc.player?.sendSystemMessage(Component.literal("§8[§dSixEyes§8] §f$message"))
    }

    fun success(message: String) {
        mc.player?.sendSystemMessage(Component.literal("§8[§dSixEyes§8] §a$message"))
    }

    fun error(message: String) {
        mc.player?.sendSystemMessage(Component.literal("§8[§dSixEyes§8] §c$message"))
    }

    fun info(message: String) {
        mc.player?.sendSystemMessage(Component.literal("§8[§dSixEyes§8] §b$message"))
    }

    fun raw(message: String) {
        mc.player?.sendSystemMessage(Component.literal(message))
    }
}
