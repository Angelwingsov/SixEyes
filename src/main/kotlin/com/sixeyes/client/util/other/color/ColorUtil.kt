package com.sixeyes.client.util.other.color

import java.awt.Color

object ColorUtil {
    const val WHITE: Int = -0x1
    @JvmField
    val hexColors = IntArray(16)

    init {
        repeat(16) { i ->
            val baseColor = (i shr 3 and 1) * 85
            val red = (i shr 2 and 1) * 170 + baseColor + if (i == 6) 85 else 0
            val green = (i shr 1 and 1) * 170 + baseColor
            val blue = (i and 1) * 170 + baseColor

            hexColors[i] = red and 255 shl 16 or (green and 255 shl 8) or (blue and 255)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun rgba(r: Int, g: Int, b: Int, a: Int = 255): Int = Color4b(r, g, b, a).argb

    @JvmStatic
    fun shadow(color: Int): Int {
        val src = Color4b(color)
        return Color4b(0, 0, 0, (src.a * 0.6f).toInt().coerceIn(0, 255)).argb
    }

    @JvmStatic
    fun toNormalizedRgba(color: Int): FloatArray {
        val c = Color4b(color)
        return floatArrayOf(c.r / 255f, c.g / 255f, c.b / 255f, c.a / 255f)
    }
}

@JvmOverloads
fun rainbow(alpha: Float = 1f): Color4b {
    return Color4b.ofHSB(
        hue = (System.nanoTime().toDouble() / 10_000_000_000.0).toFloat() % 1.0F,
        saturation = 1F,
        brightness = 1F,
        alpha = alpha,
    )
}

fun shiftHue(color4b: Color4b, shift: Int): Color4b {
    val hsb = Color.RGBtoHSB(color4b.r, color4b.g, color4b.b, null)
    return Color4b.ofHSB(
        hue = (hsb[0] + shift.toFloat() / 360) % 1F,
        saturation = hsb[1],
        brightness = hsb[2],
        alpha = color4b.a / 255F,
    )
}

fun interpolateHue(primaryColor: Color4b, otherColor: Color4b, percentageOther: Float): Color4b {
    val hsb1 = FloatArray(3)
    val hsb2 = FloatArray(3)
    Color.RGBtoHSB(primaryColor.r, primaryColor.g, primaryColor.b, hsb1)
    Color.RGBtoHSB(otherColor.r, otherColor.g, otherColor.b, hsb2)

    val h = hsb1[0] + (hsb2[0] - hsb1[0]) * percentageOther
    val s = hsb1[1] + (hsb2[1] - hsb1[1]) * percentageOther
    val v = hsb1[2] + (hsb2[2] - hsb1[2]) * percentageOther
    val alpha = primaryColor.a + (otherColor.a - primaryColor.a) * percentageOther

    val rgb = Color.HSBtoRGB(h, s, v)
    return Color4b(
        (rgb shr 16) and 0xFF,
        (rgb shr 8) and 0xFF,
        rgb and 0xFF,
        alpha.toInt(),
    )
}
