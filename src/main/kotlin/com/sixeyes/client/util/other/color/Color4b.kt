package com.sixeyes.client.util.other.color

import net.minecraft.network.chat.TextColor
import net.minecraft.util.ARGB
import org.joml.Vector4f
import java.awt.Color

data class Color4b(val argb: Int) {

    @JvmOverloads
    constructor(r: Int, g: Int, b: Int, a: Int = 255) : this(ARGB.color(a, r, g, b))

    constructor(color: Color) : this(color.rgb)

    @get:JvmName("a")
    val a: Int get() = ARGB.alpha(argb)

    @get:JvmName("r")
    val r: Int get() = ARGB.red(argb)

    @get:JvmName("g")
    val g: Int get() = ARGB.green(argb)

    @get:JvmName("b")
    val b: Int get() = ARGB.blue(argb)

    companion object {

        val WHITE = Color4b(255, 255, 255, 255)
        val BLACK = Color4b(0, 0, 0, 255)
        val RED = Color4b(255, 0, 0, 255)
        val GREEN = Color4b(0, 255, 0, 255)
        val BLUE = Color4b(0, 0, 255, 255)
        val CYAN = Color4b(0, 255, 255, 255)
        val YELLOW = Color4b(255, 255, 0, 255)
        val ORANGE = Color4b(255, 165, 0, 255)
        val PURPLE = Color4b(128, 0, 128, 255)
        val PINK = Color4b(255, 192, 203, 255)
        val GRAY = Color4b(128, 128, 128, 255)
        val LIGHT_GRAY = Color4b(192, 192, 192, 255)
        val DARK_GRAY = Color4b(64, 64, 64, 255)
        val TRANSPARENT = Color4b(0)

        @Throws(IllegalArgumentException::class)
        fun fromHex(hex: String): Color4b {
            val cleanHex = hex.removePrefix("#")
            val hasAlpha = cleanHex.length == 8

            require(cleanHex.length == 6 || hasAlpha)

            return if (hasAlpha) {
                val argb = cleanHex.toLong(16)
                Color4b(argb.toInt())
            } else {
                val rgb = cleanHex.toInt(16)
                fullAlpha(rgb)
            }
        }

        @JvmStatic
        @JvmOverloads
        fun ofHSB(
            hue: Float,
            saturation: Float,
            brightness: Float,
            alpha: Float = 1f,
        ): Color4b {
            val rgb = Color.HSBtoRGB(hue, saturation, brightness)
            val clampedAlpha = alpha.coerceIn(0f, 1f)
            return Color4b(
                r = (rgb shr 16) and 0xFF,
                g = (rgb shr 8) and 0xFF,
                b = rgb and 0xFF,
                a = (clampedAlpha * 255).toInt(),
            )
        }

        /**
         * Creates a color with full alpha (255).
         */
        @JvmStatic
        fun fullAlpha(rgb: Int): Color4b = Color4b(rgb or 0xFF000000.toInt())
    }

    val isTransparent: Boolean
        get() = a <= 0

    fun with(
        r: Int = this.r,
        g: Int = this.g,
        b: Int = this.b,
        a: Int = this.a
    ): Color4b = Color4b(r, g, b, a)

    fun alpha(alpha: Int) = with(a = alpha)

    fun fade(fade: Float): Color4b {
        return if (fade >= 1.0f) {
            this
        } else {
            alpha((a * fade).toInt())
        }
    }

    fun darker() = Color4b(darkerChannel(r), darkerChannel(g), darkerChannel(b), a)

    private fun darkerChannel(value: Int) = (value * 0.7).toInt().coerceAtLeast(0)

    fun interpolateTo(other: Color4b, percentage: Double): Color4b =
        interpolateTo(other, percentage, percentage, percentage, percentage)

    fun interpolateTo(
        other: Color4b,
        tR: Double,
        tG: Double,
        tB: Double,
        tA: Double
    ): Color4b = Color4b(
        (r + (other.r - r) * tR).toInt().coerceIn(0, 255),
        (g + (other.g - g) * tG).toInt().coerceIn(0, 255),
        (b + (other.b - b) * tB).toInt().coerceIn(0, 255),
        (a + (other.a - a) * tA).toInt().coerceIn(0, 255)
    )

    fun toAwtColor(): Color = Color(r, g, b, a)

    fun toTextColor(): TextColor = TextColor.fromRgb(argb and 0x00FFFFFF)
    
    @JvmOverloads
    fun toHexString(format: HexFormat = HexFormat.Default): String =
        argb.toHexString(format)

    @JvmOverloads
    fun toVector4f(dest: Vector4f = Vector4f()): Vector4f {
        return dest.set(r / 255f, g / 255f, b / 255f, a / 255f)
    }
}
