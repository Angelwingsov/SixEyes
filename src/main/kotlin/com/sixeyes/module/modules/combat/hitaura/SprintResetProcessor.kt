package com.sixeyes.module.modules.combat.hitaura

import com.sixeyes.client.extensions.inGame
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.extensions.player
import com.sixeyes.client.extensions.sendPacket
import com.sixeyes.client.util.movement.MoveUtil
import com.sixeyes.module.modules.combat.HitAuraModule.SprintResetMode
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket

class SprintResetProcessor {
    private var legitResetTicks: Int = 0
    private var wasSprintingBeforeAttack: Boolean = false

    fun isResettingSprint(): Boolean = legitResetTicks > 0

    fun reset() {
        if (legitResetTicks > 0 && inGame) {
            mc.options.keySprint.isDown = MoveUtil.isKeyPressed(mc.options.keySprint)
        }
        legitResetTicks = 0
        wasSprintingBeforeAttack = false
    }

    fun onTick() {
        if (!inGame) return
        if (legitResetTicks > 0) {
            legitResetTicks--
            player.isSprinting = false
            mc.options.keySprint.isDown = false
            if (legitResetTicks == 0) {
                mc.options.keySprint.isDown = MoveUtil.isKeyPressed(mc.options.keySprint)
            }
        }
    }

    fun preAttack(mode: SprintResetMode) {
        if (!inGame || mode == SprintResetMode.OFF) return

        wasSprintingBeforeAttack = player.isSprinting || player.wasSprinting
        if (!wasSprintingBeforeAttack) return

        when (mode) {
            SprintResetMode.PACKET -> {
                if (player.wasSprinting) {
                    sendPacket(
                        ServerboundPlayerCommandPacket(
                            player,
                            ServerboundPlayerCommandPacket.Action.STOP_SPRINTING
                        )
                    )
                    player.wasSprinting = false
                }
                player.isSprinting = false
            }
            SprintResetMode.LEGIT -> {
                player.isSprinting = false
                mc.options.keySprint.isDown = false
                legitResetTicks = 2
            }
            SprintResetMode.OFF -> {}
        }
    }

    fun postAttack(mode: SprintResetMode) {
        if (!inGame || mode == SprintResetMode.OFF) return
        if (!wasSprintingBeforeAttack) return

        if (mode == SprintResetMode.PACKET) {
            player.isSprinting = true
            sendPacket(
                ServerboundPlayerCommandPacket(
                    player,
                    ServerboundPlayerCommandPacket.Action.START_SPRINTING
                )
            )
            player.wasSprinting = true
        }

        wasSprintingBeforeAttack = false
    }
}
