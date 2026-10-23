package com.sixeyes.event.events

import com.sixeyes.event.types.EventCancellable
import net.minecraft.network.protocol.Packet

class PacketReceiveEvent(
    val packet: Packet<*>
) : EventCancellable()
