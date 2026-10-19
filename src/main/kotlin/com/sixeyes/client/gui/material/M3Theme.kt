package com.sixeyes.client.gui.material

import com.sixeyes.client.util.other.color.Color4b

data class M3Palette(
    val name: String,
    val surface: Color4b,
    val surfaceDim: Color4b,
    val surfaceBright: Color4b,
    val surfaceContainerLowest: Color4b,
    val surfaceContainerLow: Color4b,
    val surfaceContainer: Color4b,
    val surfaceContainerHigh: Color4b,
    val surfaceContainerHighest: Color4b,
    val onSurface: Color4b,
    val onSurfaceVariant: Color4b,
    val outline: Color4b,
    val outlineVariant: Color4b,
    val primary: Color4b,
    val onPrimary: Color4b,
    val primaryContainer: Color4b,
    val onPrimaryContainer: Color4b,
    val secondary: Color4b,
    val secondaryContainer: Color4b,
    val onSecondaryContainer: Color4b,
    val tertiary: Color4b,
    val tertiaryContainer: Color4b,
    val error: Color4b = Color4b.fromHex("#f2b8b5"),
    val scrim: Color4b = Color4b(0, 0, 0, 160),
    val stateHover: Color4b = Color4b(255, 255, 255, 18),
    val statePressed: Color4b = Color4b(255, 255, 255, 32),
    val stateFocus: Color4b = Color4b(255, 255, 255, 25)
)

object M3Theme {
    val MATERIAL = M3Palette(
        name = "Material",
        surface = Color4b(20, 18, 24, 245),
        surfaceDim = Color4b(17, 15, 21, 250),
        surfaceBright = Color4b(59, 56, 62, 255),
        surfaceContainerLowest = Color4b(15, 13, 19, 250),
        surfaceContainer = Color4b(33, 31, 38, 250),
        surfaceContainerHigh = Color4b(43, 41, 48, 255),
        surfaceContainerHighest = Color4b(54, 52, 59, 255),
        onSurface = Color4b(230, 224, 233, 255),
        onSurfaceVariant = Color4b(202, 196, 208, 255),
        outline = Color4b(147, 143, 153, 255),
        outlineVariant = Color4b(73, 69, 79, 140),
        primary = Color4b.fromHex("#d0bcff"),
        onPrimary = Color4b.fromHex("#381e72"),
        primaryContainer = Color4b.fromHex("#4f378b"),
        onPrimaryContainer = Color4b.fromHex("#eaddff"),
        secondary = Color4b.fromHex("#ccc2dc"),
        secondaryContainer = Color4b.fromHex("#4a4458"),
        onSecondaryContainer = Color4b.fromHex("#e8def8"),
        tertiary = Color4b.fromHex("#efb8c8"),
        tertiaryContainer = Color4b.fromHex("#633b48")
    )

    val current: M3Palette = MATERIAL
}
