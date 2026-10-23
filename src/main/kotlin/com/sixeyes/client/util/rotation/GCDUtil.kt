package com.sixeyes.client.util.rotation

import com.sixeyes.client.extensions.mc
import net.minecraft.util.Mth
import kotlin.math.roundToInt

object GCDUtil {
    fun getSensitivity(): Double {
        return mc.options.sensitivity().get()
    }

    fun getGCD(): Float {
        val f = (getSensitivity() * 0.6 + 0.2).toFloat()
        return f * f * f * 8.0f * 0.15f
    }

    fun applyGCD(target: Rotation, previous: Rotation): Rotation {
        val gcd = getGCD()
        if (gcd <= 1e-6f) return target.clamped()

        val deltaYaw = Mth.wrapDegrees(target.yaw - previous.yaw)
        val deltaPitch = target.pitch - previous.pitch

        val fixedDeltaYaw = (deltaYaw / gcd).roundToInt() * gcd
        val fixedDeltaPitch = (deltaPitch / gcd).roundToInt() * gcd

        val finalYaw = previous.yaw + fixedDeltaYaw
        val finalPitch = (previous.pitch + fixedDeltaPitch).coerceIn(-90.0f, 90.0f)

        return Rotation(finalYaw, finalPitch)
    }
}
