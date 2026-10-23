package com.sixeyes.client.util.rotation

import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

data class Rotation(
    var yaw: Float,
    var pitch: Float
) {
    fun clamped(): Rotation {
        return Rotation(yaw, pitch.coerceIn(-90.0f, 90.0f))
    }

    fun normalizeTo(previous: Rotation): Rotation {
        val deltaYaw = Mth.wrapDegrees(yaw - previous.yaw)
        return Rotation(previous.yaw + deltaYaw, pitch.coerceIn(-90.0f, 90.0f))
    }

    fun deltaTo(target: Rotation): Rotation {
        val dYaw = Mth.wrapDegrees(target.yaw - yaw)
        val dPitch = (target.pitch - pitch).coerceIn(-90.0f, 90.0f)
        return Rotation(dYaw, dPitch)
    }

    fun angleTo(target: Rotation): Float {
        val dYaw = Mth.wrapDegrees(target.yaw - yaw)
        val dPitch = target.pitch - pitch
        return hypot(dYaw, dPitch)
    }

    fun toDirectionVec(): Vec3 {
        val pitchRad = Math.toRadians(pitch.toDouble())
        val yawRad = Math.toRadians(-yaw.toDouble())
        val cosPitch = cos(pitchRad)
        val sinPitch = sin(pitchRad)
        val cosYaw = cos(yawRad)
        val sinYaw = sin(yawRad)
        return Vec3(sinYaw * cosPitch, -sinPitch, cosYaw * cosPitch)
    }
}
