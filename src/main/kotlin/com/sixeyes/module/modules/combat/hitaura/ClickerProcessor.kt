package com.sixeyes.module.modules.combat.hitaura

import com.sixeyes.client.extensions.inGame
import com.sixeyes.client.extensions.player
import com.sixeyes.client.util.other.TimerUtil
import com.sixeyes.module.modules.combat.HitAuraModule.ClickMode
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.roundToLong

class ClickerProcessor {
    private val clickTimer = TimerUtil()
    private var nextDelayMs: Long = 80L

    fun reset() {
        clickTimer.reset()
        nextDelayMs = 50L
    }

    fun isReadyToClick(
        clickMode: ClickMode,
        cps: Double,
        randomize: Boolean,
        randomSpread: Double
    ): Boolean {
        if (!inGame) return false

        return when (clickMode) {
            ClickMode.MODE_1_9 -> {
                val cooldownReady = player.getAttackStrengthScale(0.5f) >= 0.91f
                cooldownReady && clickTimer.isComplete(45L)
            }
            ClickMode.MODE_1_8 -> {
                clickTimer.isComplete(nextDelayMs)
            }
        }
    }

    fun onAttackExecuted(
        clickMode: ClickMode,
        cps: Double,
        randomize: Boolean,
        randomSpread: Double
    ) {
        clickTimer.reset()
        if (clickMode == ClickMode.MODE_1_8) {
            nextDelayMs = computeNext18Delay(cps, randomize, randomSpread)
        } else {
            val rng = ThreadLocalRandom.current()
            nextDelayMs = rng.nextLong(40L, 65L)
        }
    }

    private fun computeNext18Delay(cps: Double, randomize: Boolean, randomSpread: Double): Long {
        val safeCps = cps.coerceIn(1.0, 25.0)
        if (!randomize || randomSpread <= 0.01) {
            return (1000.0 / safeCps).roundToLong().coerceAtLeast(25L)
        }

        val rng = ThreadLocalRandom.current()
        val gaussian = rng.nextGaussian() * (randomSpread * 0.65)
        val spike = when {
            rng.nextDouble() < 0.08 -> -randomSpread * 0.85
            rng.nextDouble() < 0.10 -> randomSpread * 0.95
            else -> 0.0
        }

        val effectiveCps = (safeCps + gaussian + spike).coerceIn(1.0, 28.0)
        return (1000.0 / effectiveCps).roundToLong().coerceIn(25L, 1000L)
    }
}
