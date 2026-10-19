package com.sixeyes.module.setting

import java.util.Locale

class NumberSetting(
    name: String,
    defaultValue: Double,
    val min: Double,
    val max: Double,
    val step: Double = 0.1,
    val precision: Int = 1,
    val unit: String = "",
    description: String = "",
    isVisible: () -> Boolean = { true }
) : Setting<Double>(name, defaultValue, defaultValue, description, isVisible) {

    fun getFloat(): Float = value.toFloat()
    fun getInt(): Int = value.toInt()

    override fun set(newValue: Double) {
        val clamped = newValue.coerceIn(min, max)
        val stepped = if (step > 0) {
            Math.round(clamped / step) * step
        } else {
            clamped
        }
        super.set(stepped)
    }

    fun getFormattedValue(): String {
        return if (precision <= 0) {
            "${value.toInt()}$unit"
        } else {
            String.format(Locale.US, "%.${precision}f$unit", value)
        }
    }
}
