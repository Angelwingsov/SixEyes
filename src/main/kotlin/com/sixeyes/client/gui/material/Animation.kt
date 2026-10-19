package com.sixeyes.client.gui.material

import com.sixeyes.client.util.other.color.Color4b
import kotlin.math.abs
import kotlin.math.exp

class AnimFloat(
    var value: Float = 0f,
    var target: Float = value,
    var speed: Float = 16f
) {
    fun setImmediate(v: Float) {
        value = v
        target = v
    }

    fun update(targetVal: Float, deltaSeconds: Float, customSpeed: Float = speed): Float {
        target = targetVal
        val dt = deltaSeconds.coerceIn(0.0001f, 0.1f)
        val factor = (1f - exp((-customSpeed * dt).toDouble())).toFloat().coerceIn(0f, 1f)
        value += (target - value) * factor
        if (abs(target - value) < 0.0005f) {
            value = target
        }
        return value
    }

    fun get(): Float = value
}

class AnimColor(
    var value: Color4b = Color4b.WHITE,
    var target: Color4b = value,
    var speed: Float = 16f
) {
    private val r = AnimFloat(value.r.toFloat(), value.r.toFloat(), speed)
    private val g = AnimFloat(value.g.toFloat(), value.g.toFloat(), speed)
    private val b = AnimFloat(value.b.toFloat(), value.b.toFloat(), speed)
    private val a = AnimFloat(value.a.toFloat(), value.a.toFloat(), speed)

    fun update(targetColor: Color4b, deltaSeconds: Float): Color4b {
        target = targetColor
        val curR = r.update(target.r.toFloat(), deltaSeconds, speed).toInt().coerceIn(0, 255)
        val curG = g.update(target.g.toFloat(), deltaSeconds, speed).toInt().coerceIn(0, 255)
        val curB = b.update(target.b.toFloat(), deltaSeconds, speed).toInt().coerceIn(0, 255)
        val curA = a.update(target.a.toFloat(), deltaSeconds, speed).toInt().coerceIn(0, 255)
        value = Color4b(curR, curG, curB, curA)
        return value
    }

    fun get(): Color4b = value
}
