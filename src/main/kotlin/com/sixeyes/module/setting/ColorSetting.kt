package com.sixeyes.module.setting

import com.sixeyes.client.util.other.color.Color4b
import java.awt.Color
import java.util.Locale

typealias ColorPickerSetting = ColorSetting

class ColorSetting(
    name: String,
    defaultValue: Color4b,
    val hasAlpha: Boolean = true,
    description: String = "",
    isVisible: () -> Boolean = { true }
) : Setting<Color4b>(name, defaultValue, defaultValue, description, isVisible) {

    constructor(
        name: String,
        r: Int,
        g: Int,
        b: Int,
        a: Int = 255,
        hasAlpha: Boolean = true,
        description: String = "",
        isVisible: () -> Boolean = { true }
    ) : this(name, Color4b(r, g, b, a), hasAlpha, description, isVisible)

    constructor(
        name: String,
        hex: String,
        hasAlpha: Boolean = true,
        description: String = "",
        isVisible: () -> Boolean = { true }
    ) : this(name, Color4b.fromHex(hex), hasAlpha, description, isVisible)

    val r: Int get() = value.r
    val g: Int get() = value.g
    val b: Int get() = value.b
    val a: Int get() = value.a
    val rgb: Int get() = value.argb

    fun getHSB(dest: FloatArray = FloatArray(3)): FloatArray =
        Color.RGBtoHSB(r, g, b, dest)

    fun getHue(): Float = getHSB()[0]

    fun getSaturation(): Float = getHSB()[1]

    fun getBrightness(): Float = getHSB()[2]

    fun setHSB(hue: Float, saturation: Float, brightness: Float, alpha: Float = 1f) {
        set(Color4b.ofHSB(hue, saturation, brightness, alpha))
    }

    fun setRGBA(r: Int, g: Int, b: Int, a: Int = 255) {
        set(Color4b(r, g, b, a))
    }

    fun toHex(): String {
        return if (hasAlpha) {
            String.format(Locale.US, "#%02X%02X%02X%02X", value.r, value.g, value.b, value.a)
        } else {
            String.format(Locale.US, "#%02X%02X%02X", value.r, value.g, value.b)
        }
    }
}
