package com.sixeyes.module.modules.movement

import com.sixeyes.client.extensions.MOVEMENT
import com.sixeyes.client.extensions.inGame
import com.sixeyes.client.extensions.player
import com.sixeyes.module.Module

object NoSlowModule : Module(
    name = "NoSlow",
    category = MOVEMENT,
    description = ""
) {
    val mode = mode(
        name = "Mode",
        value = "Grim Tick",
        modes = listOf("Grim Tick", "Vanilla"),
        description = ""
    )

    fun shouldCancelSlowdown(): Boolean {
        if (!isEnabled() || !inGame) return false
        if (!player.isUsingItem || player.isPassenger) return false

        return when {
            mode.isMode("Vanilla") -> true
            mode.isMode("Grim Tick") -> player.ticksUsingItem > 0 && player.ticksUsingItem % 2 == 0
            else -> false
        }
    }
}
