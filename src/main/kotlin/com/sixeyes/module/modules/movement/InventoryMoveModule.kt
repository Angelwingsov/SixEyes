package com.sixeyes.module.modules.movement

import com.sixeyes.client.extensions.MOVEMENT
import com.sixeyes.client.extensions.currentScreen
import com.sixeyes.client.extensions.inGame
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.extensions.sendPacket
import com.sixeyes.client.util.movement.MoveUtil
import com.sixeyes.client.util.other.TimerUtil
import com.sixeyes.event.events.PacketSendEvent
import com.sixeyes.event.events.PlayerUpdateEvent
import com.sixeyes.event.events.ScreenCloseEvent
import com.sixeyes.module.Module
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen
import net.minecraft.client.gui.screens.inventory.AnvilScreen
import net.minecraft.client.gui.screens.inventory.BookEditScreen
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket
import net.minecraft.network.protocol.game.ServerboundContainerSlotStateChangedPacket
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket
import sweetie.evaware.flora.api.Commando
import java.util.ArrayDeque
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

object InventoryMoveModule : Module(
    name = "Inventory Move",
    category = MOVEMENT,
    description = ""
) {
    val sneak = boolean("Sneak", false, "")
    val slowness = boolean("Slowness", false, "")

    private val packetQueue = ArrayDeque<Packet<*>>()
    private val timerUtil = TimerUtil()
    private var slowed = false
    private var flushing = false
    private var closingPending = false

    override fun onDisable() {
        flushPackets()
        slowed = false
        closingPending = false
        MoveUtil.resetMovementKeys()
    }

    @Commando
    fun onUpdate(event: PlayerUpdateEvent) {
        if (!inGame) return

        if (!timerUtil.isComplete(100) && slowness.value) {
            slowed = true
            MoveUtil.resetMovementKeys()
            mc.player?.isSprinting = false
            return
        }

        slowed = false

        if (packetQueue.isNotEmpty() && !flushing && !MoveUtil.isMoving() && !mc.options.keyJump.isDown && timerUtil.isComplete(100)) {
            flushPackets()
        }

        val screen = currentScreen ?: return
        if (isTypingScreen(screen)) return
        MoveUtil.updateMovementKeys(includeSneak = sneak.value)
        if (slowness.value && screen is AbstractContainerScreen<*>) {
            mc.options.keySprint.isDown = false
            mc.player?.isSprinting = false
        }
    }

    @Commando
    fun onPacket(event: PacketSendEvent) {
        if (!slowness.value || flushing) return

        val packet = event.packet
        if (isContainerActionPacket(packet) && currentScreen is AbstractContainerScreen<*>) {
            val isMovingOrHolding = MoveUtil.isMoving()
                || mc.options.keyJump.isDown
                || MoveUtil.movementKeys.any(MoveUtil::isKeyPressed)

            if (isMovingOrHolding) {
                packetQueue.addLast(packet)
                event.cancel = true
                timerUtil.reset()
                slowed = true
                MoveUtil.resetMovementKeys()
                mc.player?.isSprinting = false
            }
        }
    }

    @Commando
    fun onCloseScreen(event: ScreenCloseEvent) {
        val screen = event.screen
        if (screen is AbstractContainerScreen<*> && packetQueue.isNotEmpty() && slowness.value && !flushing) {
            event.cancel = true
            if (closingPending) return

            closingPending = true
            slowed = true
            timerUtil.reset()
            MoveUtil.resetMovementKeys()

            CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS, mc).execute {
                flushPackets()
                slowed = false
                closingPending = false
                screen.onClose()
            }
        }
    }

    fun slownessIsEnabled(): Boolean = slowness.value

    fun shouldPreventSprint(): Boolean {
        if (!isEnabled() || !slowness.value) return false
        return slowed || currentScreen is AbstractContainerScreen<*>
    }

    fun canMoveInScreen(screen: Screen): Boolean {
        return isEnabled() && !isTypingScreen(screen)
    }

    private fun flushPackets() {
        if (packetQueue.isEmpty()) return
        flushing = true
        try {
            while (packetQueue.isNotEmpty()) {
                sendPacket(packetQueue.removeFirst())
            }
        } finally {
            flushing = false
        }
    }

    private fun isContainerActionPacket(packet: Packet<*>): Boolean {
        return packet is ServerboundContainerClickPacket
            || packet is ServerboundContainerButtonClickPacket
            || packet is ServerboundSetCreativeModeSlotPacket
            || packet is ServerboundContainerSlotStateChangedPacket
    }

    private fun isTypingScreen(screen: Screen): Boolean {
        return screen is ChatScreen
            || screen is AbstractSignEditScreen
            || screen is AnvilScreen
            || screen is AbstractCommandBlockEditScreen
            || screen is StructureBlockEditScreen
            || screen is BookEditScreen
            || screen.children().any { it is EditBox && it.isFocused }
    }
}
