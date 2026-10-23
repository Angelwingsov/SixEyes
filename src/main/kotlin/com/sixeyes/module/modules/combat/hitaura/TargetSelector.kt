package com.sixeyes.module.modules.combat.hitaura

import com.sixeyes.client.extensions.inGame
import com.sixeyes.client.extensions.level
import com.sixeyes.client.extensions.player
import com.sixeyes.client.util.rotation.RotationUtil
import com.sixeyes.module.modules.combat.HitAuraModule.TargetMode
import com.sixeyes.module.modules.combat.HitAuraModule.TargetPriority
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ambient.AmbientCreature
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.npc.Npc
import net.minecraft.world.entity.player.Player

class TargetSelector {
    var currentTarget: LivingEntity? = null
        private set

    private var switchIndex: Int = 0
    private var lastSwitchTime: Long = 0L

    fun reset() {
        currentTarget = null
        switchIndex = 0
    }

    fun updateTarget(
        aimRange: Double,
        fov: Float,
        priority: TargetPriority,
        targetMode: TargetMode,
        allowedTargets: Set<String>,
        throughWalls: Boolean
    ): LivingEntity? {
        if (!inGame) {
            currentTarget = null
            return null
        }

        if (targetMode == TargetMode.SINGLE) {
            val locked = currentTarget
            if (locked != null && isValidTarget(locked, aimRange, fov, allowedTargets, throughWalls)) {
                return locked
            }
        }

        val candidates = ArrayList<LivingEntity>()
        for (entity in level.entitiesForRendering()) {
            if (entity is LivingEntity && isValidTarget(entity, aimRange, fov, allowedTargets, throughWalls)) {
                candidates.add(entity)
            }
        }

        if (candidates.isEmpty()) {
            currentTarget = null
            return null
        }

        candidates.sortWith(getComparator(priority))

        currentTarget = when (targetMode) {
            TargetMode.SINGLE -> candidates.first()
            TargetMode.SWITCH -> {
                val now = System.currentTimeMillis()
                if (now - lastSwitchTime >= 350L) {
                    switchIndex = (switchIndex + 1) % candidates.size
                    lastSwitchTime = now
                }
                candidates[switchIndex % candidates.size]
            }
        }

        return currentTarget
    }

    fun isValidTarget(
        entity: Entity,
        maxRange: Double,
        fov: Float,
        allowedTargets: Set<String>,
        throughWalls: Boolean
    ): Boolean {
        if (entity === player) return false
        if (entity !is LivingEntity) return false
        if (!entity.isAlive || entity.isRemoved || entity.health <= 0.0f) return false
        if (entity is ArmorStand) return false
        if (entity.isSpectator) return false

        if (!throughWalls && !player.hasLineOfSight(entity)) {
            return false
        }

        if (entity.isInvisible && !allowedTargets.contains("Invisibles")) {
            return false
        }

        val matchesType = when (entity) {
            is Player -> {
                if (!allowedTargets.contains("Players")) false
                else if (isNaked(entity) && !allowedTargets.contains("Naked")) false
                else true
            }
            is Enemy -> allowedTargets.contains("Mobs")
            is Animal, is AgeableMob, is AmbientCreature, is Npc -> allowedTargets.contains("Animals")
            is Mob -> {
                if (entity.type.category.isFriendly) allowedTargets.contains("Animals")
                else allowedTargets.contains("Mobs")
            }
            else -> false
        }
        if (!matchesType) return false

        val dist = RotationUtil.getDistanceToBox(entity)
        if (dist > maxRange) return false

        if (!RotationUtil.isInFov(entity, fov)) return false

        return true
    }

    private fun isNaked(target: Player): Boolean {
        return target.armorValue == 0
    }

    private fun getComparator(priority: TargetPriority): Comparator<LivingEntity> {
        return when (priority) {
            TargetPriority.DISTANCE -> compareBy { RotationUtil.getDistanceToBox(it) }
            TargetPriority.HEALTH -> compareBy { (it.health + it.absorptionAmount).toDouble() }
            TargetPriority.FOV -> compareBy { RotationUtil.getFovToEntity(it).toDouble() }
            TargetPriority.HURT_TIME -> compareBy<LivingEntity> { it.hurtTime }.thenBy { RotationUtil.getDistanceToBox(it) }
            TargetPriority.ARMOR -> compareBy<LivingEntity> { it.armorValue }.thenBy { RotationUtil.getDistanceToBox(it) }
        }
    }
}
