package com.sixeyes.client.extensions

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.MultiPlayerGameMode
import net.minecraft.client.player.ClientInput
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.protocol.Packet
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

inline val mc: Minecraft
    get() = Minecraft.getInstance()

inline val player: LocalPlayer
    get() = mc.player!!

inline val level: ClientLevel
    get() = mc.level!!

inline val gameMode: MultiPlayerGameMode
    get() = mc.gameMode!!

@Deprecated("Use gameMode instead", ReplaceWith("gameMode"))
inline val gameMod: MultiPlayerGameMode
    get() = gameMode

inline val currentScreen: Screen?
    get() = mc.gui.screen()

inline val inGame: Boolean
    get() = mc.player != null && mc.level != null

fun sendPacket(packet: Packet<*>) {
    mc.player?.connection?.send(packet)
}

inline val ClientInput.movementForward: Float
    get() = moveVector.y

inline val ClientInput.movementSideways: Float
    get() = moveVector.x

inline val LocalPlayer.moving: Boolean
    get() = input.movementForward != 0.0f || input.movementSideways != 0.0f

inline val Entity.lastPos: Vec3
    get() = Vec3(xo, yo, zo)

inline val Entity.box: AABB
    get() = boundingBox.inflate(pickRadius.toDouble())
