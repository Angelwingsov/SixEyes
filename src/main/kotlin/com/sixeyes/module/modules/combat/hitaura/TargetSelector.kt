package com.sixeyes.module.modules.combat.hitaura

import com.sixeyes.client.extensions.inGame
import com.sixeyes.client.extensions.mc
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
    private val candidateList = ArrayList<LivingEntity>()

    fun reset() {
        currentTarget = null
        switchIndex = 0
        candidateList.clear()
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
            candidateList.clear()
            return null
        }

        if (targetMode == TargetMode.SINGLE) {
            val locked = currentTarget
            if (locked != null && isValidTarget(locked, aimRange, fov, allowedTargets, throughWalls)) {
                return locked
            }
        }

        val currentLevel = mc.level ?: run {
            currentTarget = null
            candidateList.clear()
            return null
        }

        candidateList.clear()
        for (entity in currentLevel.entitiesForRendering()) {
            if (entity is LivingEntity && isValidTarget(entity, aimRange, fov, allowedTargets, throughWalls)) {
                candidateList.add(entity)
            }
        }

        if (candidateList.isEmpty()) {
            currentTarget = null
            return null
        }

        val comparator = getComparator(priority)

        currentTarget = when (targetMode) {
            TargetMode.SINGLE -> candidateList.minWithOrNull(comparator)
            TargetMode.SWITCH -> {
                candidateList.sortWith(comparator)
                val now = System.currentTimeMillis()
                if (now - lastSwitchTime >= 350L) {
                    switchIndex = (switchIndex + 1) % candidateList.size
                    lastSwitchTime = now
                }
                candidateList[switchIndex % candidateList.size]
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
        val localPlayer = mc.player ?: return false
        if (entity === localPlayer) return false
        if (entity !is LivingEntity) return false
        if (!entity.isAlive || entity.isRemoved || entity.health <= 0.0f) return false
        if (entity is ArmorStand) return false
        if (entity.isSpectator) return false

        if (!throughWalls && !localPlayer.hasLineOfSight(entity)) {
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

    companion object {
        private val DISTANCE = Comparator<LivingEntity> { a, b ->
            RotationUtil.getDistanceToBox(a).compareTo(RotationUtil.getDistanceToBox(b))
        }
        private val HEALTH = Comparator<LivingEntity> { a, b ->
            (a.health + a.absorptionAmount).compareTo(b.health + b.absorptionAmount)
        }
        private val FOV = Comparator<LivingEntity> { a, b ->
            RotationUtil.getFovToEntity(a).compareTo(RotationUtil.getFovToEntity(b))
        }
        private val HURTIME = Comparator<LivingEntity> { a, b ->
            val res = a.hurtTime.compareTo(b.hurtTime)
            if (res != 0) res else RotationUtil.getDistanceToBox(a).compareTo(RotationUtil.getDistanceToBox(b))
        }
        private val ARMOR = Comparator<LivingEntity> { a, b ->
            val res = a.armorValue.compareTo(b.armorValue)
            if (res != 0) res else RotationUtil.getDistanceToBox(a).compareTo(RotationUtil.getDistanceToBox(b))
        }

        fun getComparator(priority: TargetPriority): Comparator<LivingEntity> = when (priority) {
            TargetPriority.DISTANCE -> DISTANCE
            TargetPriority.HEALTH -> HEALTH
            TargetPriority.FOV -> FOV
            TargetPriority.HURT_TIME -> HURTIME
            TargetPriority.ARMOR -> ARMOR
        }
    }
}
