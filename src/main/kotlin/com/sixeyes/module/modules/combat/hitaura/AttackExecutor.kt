package com.sixeyes.module.modules.combat.hitaura

import com.sixeyes.client.extensions.gameMode
import com.sixeyes.client.extensions.inGame
import com.sixeyes.client.extensions.player
import com.sixeyes.client.extensions.sendPacket
import com.sixeyes.client.util.rotation.RayTraceUtil
import com.sixeyes.client.util.rotation.RotationManager
import com.sixeyes.client.util.rotation.RotationUtil
import com.sixeyes.module.modules.combat.HitAuraModule.ClickMode
import com.sixeyes.module.modules.combat.HitAuraModule.SprintResetMode
import net.minecraft.network.protocol.game.ServerboundPunchPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity

class AttackExecutor(
    private val clickerProcessor: ClickerProcessor,
    private val criticalProcessor: CriticalProcessor,
    private val sprintResetProcessor: SprintResetProcessor,
    private val rotationProcessor: AuraRotationProcessor
) {
    fun shouldPrepareAttack(
        target: LivingEntity,
        attackRange: Double,
        clickMode: ClickMode,
        cps: Double,
        randomizeCps: Boolean,
        cpsSpread: Double,
        onlyCrits: Boolean,
        smartCrits: Boolean
    ): Boolean {
        if (!inGame) return false
        if (RotationUtil.getDistanceToBox(target) > attackRange) return false
        if (!clickerProcessor.isReadyToClick(clickMode, cps, randomizeCps, cpsSpread)) return false
        if (!criticalProcessor.isReadyForAttack(onlyCrits, smartCrits)) return false
        return true
    }

    fun tryAttack(
        target: LivingEntity,
        attackRange: Double,
        clickMode: ClickMode,
        cps: Double,
        randomizeCps: Boolean,
        cpsSpread: Double,
        onlyCrits: Boolean,
        smartCrits: Boolean,
        sprintResetMode: SprintResetMode,
        rayTrace: Boolean,
        throughWalls: Boolean,
        checkAimAngle: Boolean
    ): Boolean {
        if (!shouldPrepareAttack(target, attackRange, clickMode, cps, randomizeCps, cpsSpread, onlyCrits, smartCrits)) {
            return false
        }

        if (rayTrace) {
            val currentRot = RotationManager.getServerRotation()
            val hitsCurrentHitbox = RayTraceUtil.isLookingAtEntity(
                target = target,
                rotation = currentRot,
                range = attackRange,
                throughWalls = throughWalls
            )
            if (!hitsCurrentHitbox) {
                return false
            }

            val previousRot = RotationManager.getPreviousServerRotation()
            val hitsServerHitbox = RayTraceUtil.isLookingAtEntity(
                target = target,
                rotation = previousRot,
                range = attackRange,
                throughWalls = throughWalls
            )
            if (!hitsServerHitbox) {
                return false
            }
        } else if (checkAimAngle && !rotationProcessor.isAimedAtTarget(target, 28.0f)) {
            return false
        }

        val isCritAttack = onlyCrits && criticalProcessor.isFallingForCrit()
        sprintResetProcessor.preAttack(sprintResetMode, forceStopSprint = isCritAttack)

        val itemStack = player.getItemInHand(InteractionHand.MAIN_HAND)
        val swingAnimation = itemStack.attackAnimation

        if (clickMode == ClickMode.MODE_1_8) {
            player.swing(InteractionHand.MAIN_HAND, swingAnimation, false)
            sendPacket(ServerboundPunchPacket.INSTANCE)
            gameMode.attack(player, target)
        } else {
            gameMode.attack(player, target)
            player.swing(InteractionHand.MAIN_HAND, swingAnimation, false)
            sendPacket(ServerboundPunchPacket.INSTANCE)
        }

        sprintResetProcessor.postAttack(sprintResetMode)
        clickerProcessor.onAttackExecuted(clickMode, cps, randomizeCps, cpsSpread)

        return true
    }
}
