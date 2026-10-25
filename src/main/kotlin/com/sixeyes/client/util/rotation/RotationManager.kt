package com.sixeyes.client.util.rotation

import com.sixeyes.client.extensions.inGame
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.listener.Listener
import net.minecraft.client.player.ClientInput
import net.minecraft.client.player.LocalPlayer
import net.minecraft.util.Mth
import kotlin.math.hypot

object RotationManager : Listener() {
    var currentRotation: Rotation? = null
        private set
    var previousRotation: Rotation? = null
        private set

    var activeMoveFix: MoveFixMode = MoveFixMode.SILENT
        private set
    var applyGcd: Boolean = true
        private set
    var returnSpeed: Float = 55.0f
        private set

    private var keepTicks: Int = 0
    private var updatedThisTick: Boolean = false

    private var savedYaw: Float = 0.0f
    private var savedPitch: Float = 0.0f
    private var packetRotationApplied: Boolean = false

    override fun init() {
    }

    fun isActive(): Boolean = inGame && currentRotation != null

    fun getServerRotation(): Rotation {
        return currentRotation ?: mc.player?.let { Rotation(it.yRot, it.xRot) } ?: Rotation(0.0f, 0.0f)
    }

    fun getPreviousServerRotation(): Rotation {
        return previousRotation ?: getServerRotation()
    }

    fun setRotation(
        rotation: Rotation,
        moveFix: MoveFixMode = MoveFixMode.SILENT,
        gcd: Boolean = true,
        keepTicks: Int = 1,
        returnSpeed: Float = 55.0f,
        clientLook: Boolean = false
    ) {
        if (!inGame) return
        val localPlayer = mc.player ?: return

        val basePrev = currentRotation ?: Rotation(localPlayer.yRot, localPlayer.xRot)
        val normalized = rotation.normalizeTo(basePrev)
        val finalRot = if (gcd) GCDUtil.applyGCD(normalized, basePrev) else normalized.clamped()

        this.previousRotation = basePrev
        this.currentRotation = finalRot
        this.activeMoveFix = moveFix
        this.applyGcd = gcd
        this.keepTicks = keepTicks
        this.returnSpeed = returnSpeed
        this.updatedThisTick = true

        if (clientLook) {
            localPlayer.yRot = finalRot.yaw
            localPlayer.xRot = finalRot.pitch
        }
    }

    fun resetImmediate() {
        currentRotation = null
        previousRotation = null
        keepTicks = 0
        updatedThisTick = false
        MoveFixProcessor.reset()
    }

    fun onPreUpdate() {
        if (!inGame) {
            resetImmediate()
            return
        }
        val localPlayer = mc.player ?: run {
            resetImmediate()
            return
        }
        previousRotation = currentRotation ?: Rotation(localPlayer.yRot, localPlayer.xRot)
        updatedThisTick = false
    }

    fun onPostUpdate() {
        if (!inGame) {
            resetImmediate()
            return
        }
        val localPlayer = mc.player ?: run {
            resetImmediate()
            return
        }

        val current = currentRotation ?: return
        if (updatedThisTick) return

        if (keepTicks > 0) {
            keepTicks--
            return
        }

        val cameraRot = Rotation(localPlayer.yRot, localPlayer.xRot).normalizeTo(current)
        val dYaw = Mth.wrapDegrees(cameraRot.yaw - current.yaw)
        val dPitch = cameraRot.pitch - current.pitch
        val dist = hypot(dYaw, dPitch)

        if (dist <= 1.2f || returnSpeed >= 180.0f) {
            resetImmediate()
            return
        }

        val stepFactor = (returnSpeed / dist).coerceIn(0.0f, 1.0f)
        val stepped = Rotation(
            current.yaw + dYaw * stepFactor,
            (current.pitch + dPitch * stepFactor).coerceIn(-90.0f, 90.0f)
        )
        val finalRot = if (applyGcd) GCDUtil.applyGCD(stepped, current) else stepped

        if (finalRot.angleTo(cameraRot) <= 1.2f) {
            resetImmediate()
        } else {
            currentRotation = finalRot
        }
    }

    fun onInputUpdate(input: ClientInput) {
        if (!isActive()) return
        val rot = currentRotation ?: return
        val p = mc.player ?: return
        MoveFixProcessor.applyMoveFix(input, activeMoveFix, rot.yaw, p.yRot)
    }

    fun getMovementYaw(defaultYaw: Float): Float {
        if (!isActive()) return defaultYaw
        if (activeMoveFix == MoveFixMode.OFF) return defaultYaw
        return currentRotation?.yaw ?: defaultYaw
    }

    fun onPreSendChanges(localPlayer: LocalPlayer) {
        val rot = currentRotation ?: return
        savedYaw = localPlayer.yRot
        savedPitch = localPlayer.xRot
        localPlayer.yRot = rot.yaw
        localPlayer.xRot = rot.pitch
        packetRotationApplied = true
    }

    fun onPostSendChanges(localPlayer: LocalPlayer) {
        if (!packetRotationApplied) return
        localPlayer.yRot = savedYaw
        localPlayer.xRot = savedPitch
        packetRotationApplied = false
    }
}
