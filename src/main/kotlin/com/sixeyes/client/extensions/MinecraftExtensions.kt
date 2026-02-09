package com.sixeyes.client.extensions

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.MultiPlayerGameMode
import net.minecraft.client.player.ClientInput
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

// Сократители
val mc: Minecraft get() = Minecraft.getInstance()
val player: LocalPlayer get() = mc.player!!
val level: ClientLevel get() = mc.level!!
val gameMod: MultiPlayerGameMode get() = mc.gameMode!!

// Расширения
val ClientInput.movementForward: Float
    get() = moveVector.y

val ClientInput.movementSideways: Float
    get() = moveVector.x

val LocalPlayer.moving
    get() = input.movementForward != 0.0f || input.movementSideways != 0.0f

val Entity.lastPos: Vec3
    get() = Vec3(xo, yo, zo)

val Entity.box: AABB
    get() = boundingBox.inflate(pickRadius.toDouble())