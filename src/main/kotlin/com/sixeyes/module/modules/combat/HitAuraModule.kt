package com.sixeyes.module.modules.combat

import com.sixeyes.client.extensions.COMBAT
import com.sixeyes.client.extensions.currentScreen
import com.sixeyes.client.extensions.inGame
import com.sixeyes.client.extensions.player
import com.sixeyes.client.util.rotation.MoveFixMode
import com.sixeyes.client.util.rotation.RotationManager
import com.sixeyes.event.events.PlayerUpdateEvent
import com.sixeyes.module.Module
import com.sixeyes.module.modules.combat.hitaura.*
import net.minecraft.world.entity.LivingEntity
import sweetie.evaware.flora.api.Commando

object HitAuraModule : Module(
    name = "HitAura",
    category = COMBAT,
    description = ""
) {
    enum class TargetPriority(val displayName: String) {
        DISTANCE("Distance"),
        HEALTH("Health"),
        FOV("FOV"),
        HURT_TIME("HurtTime"),
        ARMOR("Armor");

        companion object {
            val names: List<String> = entries.map { it.displayName }
            fun fromName(name: String): TargetPriority =
                entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) } ?: DISTANCE
        }
    }

    enum class TargetMode(val displayName: String) {
        SINGLE("Single"),
        SWITCH("Switch");

        companion object {
            val names: List<String> = entries.map { it.displayName }
            fun fromName(name: String): TargetMode =
                entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) } ?: SINGLE
        }
    }

    enum class RotationMode(val displayName: String) {
        SMOOTH("Smooth"),
        LINEAR("Linear"),
        GRIM("Grim"),
        VULCAN("Vulcan"),
        MATRIX("Matrix"),
        SNAP("Snap");

        companion object {
            val names: List<String> = entries.map { it.displayName }
            fun fromName(name: String): RotationMode =
                entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) } ?: SMOOTH
        }
    }

    enum class AimPointMode(val displayName: String) {
        MULTIPOINT("Multipoint"),
        NEAREST("Nearest"),
        HEAD("Head"),
        CHEST("Chest");

        companion object {
            val names: List<String> = entries.map { it.displayName }
            fun fromName(name: String): AimPointMode =
                entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) } ?: MULTIPOINT
        }
    }

    enum class ClickMode(val displayName: String) {
        MODE_1_9("1.9"),
        MODE_1_8("1.8");

        companion object {
            val names: List<String> = entries.map { it.displayName }
            fun fromName(name: String): ClickMode =
                entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) } ?: MODE_1_9
        }
    }

    enum class SprintResetMode(val displayName: String) {
        LEGIT("Legit"),
        PACKET("Packet"),
        OFF("Off");

        companion object {
            val names: List<String> = entries.map { it.displayName }
            fun fromName(name: String): SprintResetMode =
                entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) } ?: LEGIT
        }
    }

    // Target selection & Priority & FOV
    val targets = multiMode(
        name = "Targets",
        value = setOf("Players", "Mobs", "Naked"),
        modes = listOf("Players", "Mobs", "Animals", "Invisibles", "Naked"),
        description = ""
    )
    val targetMode = mode(
        name = "Target Mode",
        value = "Single",
        modes = TargetMode.names,
        description = ""
    )
    val priority = mode(
        name = "Priority",
        value = "Distance",
        modes = TargetPriority.names,
        description = ""
    )
    val fov = number(
        name = "FOV",
        value = 180.0,
        min = 15.0,
        max = 180.0,
        step = 1.0,
        precision = 0,
        unit = "°",
        description = ""
    )

    // Distances & RayTrace
    val attackRange = number(
        name = "Attack Range",
        value = 3.0,
        min = 1.0,
        max = 6.0,
        step = 0.05,
        precision = 2,
        unit = "m",
        description = ""
    )
    val aimRange = number(
        name = "Aim Range",
        value = 4.2,
        min = 1.0,
        max = 8.0,
        step = 0.05,
        precision = 2,
        unit = "m",
        description = ""
    )
    val rayTrace = boolean(
        name = "RayTrace",
        value = true,
        description = ""
    )
    val throughWalls = boolean(
        name = "Through Walls",
        value = false,
        description = ""
    )

    // Rotations, GCD & MoveFix
    val rotationMode = mode(
        name = "Rotation",
        value = "Smooth",
        modes = RotationMode.names,
        description = ""
    )
    val aimPoint = mode(
        name = "Aim Point",
        value = "Multipoint",
        modes = AimPointMode.names,
        description = ""
    )
    val rotationSpeed = number(
        name = "Rotation Speed",
        value = 75.0,
        min = 10.0,
        max = 180.0,
        step = 1.0,
        precision = 0,
        unit = "°",
        description = "",
        isVisible = { !rotationMode.isMode("Snap") }
    )
    val gcdFix = boolean(
        name = "GCD Fix",
        value = true,
        description = ""
    )
    val moveFix = mode(
        name = "Move Fix",
        value = "Silent",
        modes = listOf("Silent", "Strict", "Free", "Off"),
        description = ""
    )

    // Click Mode 1.8 / 1.9 & CPS & Randomization
    val clickMode = mode(
        name = "Click Mode",
        value = "1.9",
        modes = ClickMode.names,
        description = ""
    )
    val cps = number(
        name = "CPS",
        value = 12.0,
        min = 1.0,
        max = 20.0,
        step = 0.5,
        precision = 1,
        description = "",
        isVisible = { clickMode.isMode("1.8") }
    )
    val cpsRandomization = boolean(
        name = "Randomization",
        value = true,
        description = "",
        isVisible = { clickMode.isMode("1.8") }
    )
    val cpsSpread = number(
        name = "Random Spread",
        value = 2.0,
        min = 0.5,
        max = 5.0,
        step = 0.5,
        precision = 1,
        description = "",
        isVisible = { clickMode.isMode("1.8") && cpsRandomization.value }
    )

    // Criticals & Sprint Reset
    val onlyCrits = boolean(
        name = "Only Crits",
        value = true,
        description = ""
    )
    val smartCrits = boolean(
        name = "Smart Crits",
        value = true,
        description = "",
        isVisible = { onlyCrits.value }
    )
    val sprintReset = mode(
        name = "Sprint Reset",
        value = "Legit",
        modes = SprintResetMode.names,
        description = ""
    )

    // Modular processors
    val targetSelector = TargetSelector()
    val rotationProcessor = AuraRotationProcessor()
    val criticalProcessor = CriticalProcessor()
    val clickerProcessor = ClickerProcessor()
    val sprintResetProcessor = SprintResetProcessor()
    val attackExecutor = AttackExecutor(
        clickerProcessor = clickerProcessor,
        criticalProcessor = criticalProcessor,
        sprintResetProcessor = sprintResetProcessor,
        rotationProcessor = rotationProcessor
    )

    val currentTarget: LivingEntity?
        get() = targetSelector.currentTarget

    fun shouldPreventSprint(): Boolean {
        if (!isEnabled()) return false
        if (sprintResetProcessor.isResettingSprint()) return true
        if (onlyCrits.value && inGame && !player.onGround() && currentTarget != null) {
            return true
        }
        return false
    }

    override fun onEnable() {
        targetSelector.reset()
        rotationProcessor.reset()
        clickerProcessor.reset()
        sprintResetProcessor.reset()
    }

    override fun onDisable() {
        targetSelector.reset()
        rotationProcessor.reset()
        clickerProcessor.reset()
        sprintResetProcessor.reset()
        RotationManager.resetImmediate()
    }

    @Commando
    fun onUpdate(event: PlayerUpdateEvent) {
        if (!inGame || player.isDeadOrDying) {
            targetSelector.reset()
            return
        }

        sprintResetProcessor.onTick()

        if (currentScreen != null) {
            return
        }

        val effectiveAimRange = aimRange.value.coerceAtLeast(attackRange.value)
        val priorityMode = TargetPriority.fromName(priority.value)
        val tMode = TargetMode.fromName(targetMode.value)

        val target = targetSelector.updateTarget(
            aimRange = effectiveAimRange,
            fov = fov.getFloat(),
            priority = priorityMode,
            targetMode = tMode,
            allowedTargets = targets.value,
            throughWalls = throughWalls.value
        ) ?: return

        val rotMode = RotationMode.fromName(rotationMode.value)
        val pointMode = AimPointMode.fromName(aimPoint.value)
        val moveFixMode = MoveFixMode.fromName(moveFix.value)
        val cMode = ClickMode.fromName(clickMode.value)
        val sResetMode = SprintResetMode.fromName(sprintReset.value)

        val readyToAttackNow = attackExecutor.shouldPrepareAttack(
            target = target,
            attackRange = attackRange.value,
            clickMode = cMode,
            cps = cps.value,
            randomizeCps = cpsRandomization.value,
            cpsSpread = cpsSpread.value,
            onlyCrits = onlyCrits.value,
            smartCrits = smartCrits.value
        )

        rotationProcessor.processRotation(
            target = target,
            rotationMode = rotMode,
            aimPointMode = pointMode,
            rotationSpeed = rotationSpeed.getFloat(),
            gcdEnabled = gcdFix.value,
            moveFixMode = moveFixMode,
            shouldSnapNow = readyToAttackNow
        )

        attackExecutor.tryAttack(
            target = target,
            attackRange = attackRange.value,
            clickMode = cMode,
            cps = cps.value,
            randomizeCps = cpsRandomization.value,
            cpsSpread = cpsSpread.value,
            onlyCrits = onlyCrits.value,
            smartCrits = smartCrits.value,
            sprintResetMode = sResetMode,
            rayTrace = rayTrace.value,
            throughWalls = throughWalls.value,
            checkAimAngle = rotMode != RotationMode.SNAP
        )
    }
}
