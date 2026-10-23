package com.sixeyes.client.util.rotation

import net.minecraft.client.player.ClientInput
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Input
import net.minecraft.world.phys.Vec2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

object MoveFixProcessor {
    private const val OCTANT_THRESHOLD = 0.38268343f // sin(22.5 deg)

    private fun calculateImpulse(positive: Boolean, negative: Boolean): Float {
        return when {
            positive == negative -> 0.0f
            positive -> 1.0f
            else -> -1.0f
        }
    }

    fun applyMoveFix(
        input: ClientInput,
        mode: MoveFixMode,
        serverYaw: Float,
        cameraYaw: Float
    ) {
        when (mode) {
            MoveFixMode.OFF, MoveFixMode.STRICT -> {
                return
            }
            MoveFixMode.FREE -> {
                val rawForward = calculateImpulse(
                    input.keyPresses.forward(),
                    input.keyPresses.backward()
                )
                val rawLeft = calculateImpulse(
                    input.keyPresses.left(),
                    input.keyPresses.right()
                )
                if (rawForward == 0.0f && rawLeft == 0.0f) return

                val deltaRad = Math.toRadians((cameraYaw - serverYaw).toDouble()).toFloat()
                val cos = cos(deltaRad)
                val sin = sin(deltaRad)

                val newForward = rawForward * cos + rawLeft * sin
                val newLeft = rawLeft * cos - rawForward * sin

                input.moveVector = Vec2(newLeft, newForward).normalized()
            }
            MoveFixMode.SILENT -> {
                val rawForward = calculateImpulse(
                    input.keyPresses.forward(),
                    input.keyPresses.backward()
                )
                val rawLeft = calculateImpulse(
                    input.keyPresses.left(),
                    input.keyPresses.right()
                )
                val length = hypot(rawForward, rawLeft)
                if (length <= 1e-4f) return

                val normForward = rawForward / length
                val normLeft = rawLeft / length

                val deltaRad = Math.toRadians((cameraYaw - serverYaw).toDouble()).toFloat()
                val cos = cos(deltaRad)
                val sin = sin(deltaRad)

                val rotForward = normForward * cos + normLeft * sin
                val rotLeft = normLeft * cos - normForward * sin

                val forwardKey = rotForward > OCTANT_THRESHOLD
                val backwardKey = rotForward < -OCTANT_THRESHOLD
                val leftKey = rotLeft > OCTANT_THRESHOLD
                val rightKey = rotLeft < -OCTANT_THRESHOLD

                val currentKeys = input.keyPresses
                val sprintKey = currentKeys.sprint() && forwardKey && !backwardKey

                input.keyPresses = Input(
                    forwardKey,
                    backwardKey,
                    leftKey,
                    rightKey,
                    currentKeys.jump(),
                    currentKeys.shift(),
                    sprintKey
                )

                val f = calculateImpulse(forwardKey, backwardKey)
                val g = calculateImpulse(leftKey, rightKey)
                input.moveVector = Vec2(g, f).normalized()
            }
        }
    }

    fun getDirectionYaw(baseYaw: Float, forward: Float, sideways: Float): Float {
        var yaw = baseYaw
        if (forward != 0.0f) {
            if (sideways > 0.0f) {
                yaw += if (forward > 0.0f) -45.0f else 45.0f
            } else if (sideways < 0.0f) {
                yaw += if (forward > 0.0f) 45.0f else -45.0f
            }
            if (forward < 0.0f) {
                yaw += 180.0f
            }
        } else if (sideways != 0.0f) {
            yaw += if (sideways > 0.0f) -90.0f else 90.0f
        }
        return Mth.wrapDegrees(yaw)
    }
}
