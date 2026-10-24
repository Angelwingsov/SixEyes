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

class SafeClientContext(
    val player: LocalPlayer,
    val level: ClientLevel,
    val gameMode: MultiPlayerGameMode?
) {
    inline val mc: Minecraft get() = Minecraft.getInstance()

    fun sendPacket(packet: Packet<*>) {
        player.connection.send(packet)
    }
}

inline fun <R> safeRun(block: SafeClientContext.() -> R): R? {
    val p = mc.player ?: return null
    val l = mc.level ?: return null
    return SafeClientContext(p, l, mc.gameMode).block()
}

@Deprecated(
    "Direct player getter may throw NPE if player is null. Prefer safeRun { ... } or mc.player",
    ReplaceWith("mc.player")
)
inline val player: LocalPlayer
    get() = mc.player ?: throw IllegalStateException("LocalPlayer is null. Check inGame or use safeRun { ... }")

@Deprecated(
    "Direct level getter may throw NPE if level is null. Prefer safeRun { ... } or mc.level",
    ReplaceWith("mc.level")
)
inline val level: ClientLevel
    get() = mc.level ?: throw IllegalStateException("ClientLevel is null. Check inGame or use safeRun { ... }")

@Deprecated(
    "Direct gameMode getter may throw NPE if gameMode is null. Prefer mc.gameMode",
    ReplaceWith("mc.gameMode")
)
inline val gameMode: MultiPlayerGameMode
    get() = mc.gameMode ?: throw IllegalStateException("MultiPlayerGameMode is null.")

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
