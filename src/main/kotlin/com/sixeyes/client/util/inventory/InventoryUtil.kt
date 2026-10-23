package com.sixeyes.client.util.inventory

import com.sixeyes.client.extensions.mc
import com.sixeyes.client.util.movement.MoveUtil
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket
import net.minecraft.network.protocol.game.ServerboundUseItemPacket
import net.minecraft.tags.ItemTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.SwingAnimation

object InventoryUtil {
    private val Hotbar_rad = 0 until 9
    private val invent_rad = 9 until 36
    private val allslots = 0 until 36

    var actionDelayTimer: Long = 0L
    var resetTimer: Long = 0L
    var isSlowed: Boolean = false
    var pendingAction: Runnable? = null

    fun applySlowness(totalDelay: Long, action: Runnable) {
        if (isSlowed) return

        MoveUtil.resetMovementKeys()

        isSlowed = true
        pendingAction = action

        val now = System.currentTimeMillis()
        actionDelayTimer = now + 1
        resetTimer = now + totalDelay
    }

    fun resetKeys() {
        MoveUtil.updateMovementKeys()
        isSlowed = false
        pendingAction = null
    }

    fun swapToOffhand(slot: Int) {
        if (slot == -1) return
        val player = mc.player ?: return
        val gameMode = mc.gameMode ?: return

        val syncId = player.containerMenu.containerId
        gameMode.handleContainerInput(syncId, slot, 40, ContainerInput.SWAP, player)
        player.connection.send(ServerboundContainerClosePacket(syncId))
    }

    fun swapSlots(from: Int, to: Int, closeInvPacket: Boolean = true) {
        val player = mc.player ?: return
        val gameMode = mc.gameMode ?: return

        val syncId = player.containerMenu.containerId
        val swapSlot = when {
            to in Hotbar_rad -> to
            from in Hotbar_rad -> from
            else -> return
        }

        val slot = if (swapSlot == to) from else to
        gameMode.handleContainerInput(syncId, slot, swapSlot, ContainerInput.SWAP, player)
        if (closeInvPacket) {
            player.connection.send(ServerboundContainerClosePacket(syncId))
        }
    }

    fun useItem(hand: InteractionHand) {
        val player = mc.player ?: return
        player.connection.send(ServerboundUseItemPacket(hand, 0, player.yRot, player.xRot))
        player.swing(hand, SwingAnimation.DEFAULT, false)
    }

    fun swapToSlot(slot: Int) {
        val player = mc.player ?: return
        if (player.inventory.selectedSlot == slot) return

        player.connection.send(ServerboundSetCarriedItemPacket(slot))
        player.inventory.selectedSlot = slot
    }

    fun findItem(item: Item, inHotbar: Boolean): Int {
        val player = mc.player ?: return -1
        val items = player.inventory.nonEquipmentItems
        val range = if (inHotbar) Hotbar_rad else invent_rad
        var finalSlot = -1

        for (i in range) {
            if (items[i].item == item) {
                finalSlot = i
            }
        }
        return finalSlot
    }

    fun findItem(input: Item): Int {
        val slot = findFirstSlot(allslots) { _, stack -> stack.item == input }
        return if (slot in Hotbar_rad) slot + 36 else slot
    }

    fun hasElytraEquipped(): Boolean {
        val player = mc.player ?: return false
        return player.getItemBySlot(EquipmentSlot.CHEST).`is`(Items.ELYTRA)
    }

    fun findAxeInInventory(inHotbar: Boolean): Int {
        val range = if (inHotbar) Hotbar_rad else invent_rad
        return findFirstSlot(range) { _, stack -> stack.`is`(ItemTags.AXES) }
    }

    fun findBestSlotInHotBar(): Int {
        val emptySlot = findEmptySlot()
        return if (emptySlot != -1) emptySlot else findNonSwordSlot()
    }

    fun findEmptySlot(): Int {
        val selected = mc.player?.inventory?.selectedSlot ?: return -1
        return findFirstSlot(Hotbar_rad) { idx, stack -> stack.isEmpty && idx != selected }
    }

    private fun findNonSwordSlot(): Int {
        val selected = mc.player?.inventory?.selectedSlot ?: return -1
        return findFirstSlot(Hotbar_rad) { idx, stack -> !stack.`is`(ItemTags.SWORDS) && idx != selected }
    }

    fun countNonEnchantedTotems(): Int {
        val player = mc.player ?: return 0
        val items = player.inventory.nonEquipmentItems
        var count = 0

        for (i in allslots) {
            val stack = items[i]
            if (stack.`is`(Items.TOTEM_OF_UNDYING) && !stack.isEnchanted) {
                count++
            }
        }
        return count
    }

    fun findBestTotemSlot(saveEnchanted: Boolean): Int {
        val slot = findFirstSlot(allslots) { _, stack ->
            stack.`is`(Items.TOTEM_OF_UNDYING) && (!saveEnchanted || !stack.isEnchanted)
        }
        return if (slot in Hotbar_rad) slot + 36 else slot
    }

    private inline fun findFirstSlot(range: IntRange, predicate: (Int, ItemStack) -> Boolean): Int {
        val items = mc.player?.inventory?.nonEquipmentItems ?: return -1
        for (i in range) {
            if (predicate(i, items[i])) {
                return i
            }
        }
        return -1
    }
}
