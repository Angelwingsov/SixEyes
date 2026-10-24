package com.sixeyes.module.modules.combat

import com.sixeyes.client.extensions.COMBAT
import com.sixeyes.event.events.PacketReceiveEvent
import com.sixeyes.module.Module
import net.minecraft.network.protocol.game.ClientboundExplodePacket
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
import sweetie.evaware.flora.api.Commando

object VelocityModule : Module(
    name = "Velocity",
    category = COMBAT,
    description = ""
) {
    @Commando
    fun onPacketReceive(event: PacketReceiveEvent) {
        safeRun {
            when (val packet = event.packet) {
                is ClientboundSetEntityMotionPacket -> {
                    if (packet.id == player.id) {
                        event.cancel = true
                    }
                }
                is ClientboundExplodePacket -> {
                    val knockback = packet.playerKnockback()
                    if (knockback.isPresent) {
                        val vec = knockback.get()
                        vec.x = 0.0
                        vec.y = 0.0
                        vec.z = 0.0
                    }
                }
            }
        }
    }
}
