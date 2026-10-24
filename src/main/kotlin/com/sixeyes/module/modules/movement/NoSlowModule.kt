package com.sixeyes.module.modules.movement

import com.sixeyes.client.extensions.MOVEMENT
import com.sixeyes.module.Module

object NoSlowModule : Module(
    name = "NoSlow",
    category = MOVEMENT,
    description = ""
) {
    enum class NoSlowMode(val displayName: String) {
        GRIM_TICK("Grim Tick"),
        VANILLA("Vanilla")
    }

    val mode by enum(
        name = "Mode",
        defaultValue = NoSlowMode.GRIM_TICK,
        nameMapper = { it.displayName },
        description = ""
    )

    fun shouldCancelSlowdown(): Boolean {
        if (!isEnabled()) return false
        return safeRun {
            if (!player.isUsingItem || player.isPassenger) return@safeRun false

            when (mode) {
                NoSlowMode.VANILLA -> true
                NoSlowMode.GRIM_TICK -> player.ticksUsingItem > 0 && player.ticksUsingItem % 2 == 0
            }
        } ?: false
    }
}
