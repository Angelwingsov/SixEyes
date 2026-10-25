package com.sixeyes.client.util.render.rect

import com.sixeyes.client.util.other.color.Color4b

enum class RectType(val id: Float) {
    BASIC(0f),
    TEXTURE(1f)
}

data class RectColors(
    val topLeft: Color4b,
    val topRight: Color4b,
    val bottomRight: Color4b,
    val bottomLeft: Color4b
) {
    companion object {
        @JvmStatic
        fun solid(color: Color4b): RectColors = RectColors(color, color, color, color)
    }
}

data class RectCorners(
    val topLeft: Float,
    val topRight: Float,
    val bottomRight: Float,
    val bottomLeft: Float
) {
    companion object {
        @JvmField
        val ZERO = RectCorners(0f, 0f, 0f, 0f)

        private val COMMON_CORNERS = mapOf(
            0f to ZERO,
            2f to RectCorners(2f, 2f, 2f, 2f),
            4f to RectCorners(4f, 4f, 4f, 4f),
            6f to RectCorners(6f, 6f, 6f, 6f),
            8f to RectCorners(8f, 8f, 8f, 8f),
            10f to RectCorners(10f, 10f, 10f, 10f),
            12f to RectCorners(12f, 12f, 12f, 12f),
            14f to RectCorners(14f, 14f, 14f, 14f),
            16f to RectCorners(16f, 16f, 16f, 16f),
            20f to RectCorners(20f, 20f, 20f, 20f),
            24f to RectCorners(24f, 24f, 24f, 24f),
            28f to RectCorners(28f, 28f, 28f, 28f)
        )

        @JvmStatic
        fun all(radius: Float): RectCorners {
            val normalized = radius.coerceAtLeast(0f)
            return COMMON_CORNERS[normalized] ?: RectCorners(normalized, normalized, normalized, normalized)
        }
    }
}

data class RectBorder(
    val width: Float,
    val color: Color4b
) {
    companion object {
        @JvmField
        val NONE = RectBorder(0f, Color4b.TRANSPARENT)
    }
}

data class RectUv(
    val u0: Float,
    val v0: Float,
    val u1: Float,
    val v1: Float
) {
    companion object {
        @JvmField
        val DEFAULT = RectUv(0f, 0f, 1f, 1f)
    }
}

data class RectStyle(
    val colors: RectColors,
    val corners: RectCorners = RectCorners.ZERO,
    val border: RectBorder = RectBorder.NONE,
    val mode: Float = 0f,
    val textureId: Int = 0,
    val mix: Float = 0f,
    val alpha: Float = 1f,
    val uv: RectUv = RectUv.DEFAULT,
    val type: RectType = RectType.BASIC
) {
    companion object {
        @JvmStatic
        fun solid(color: Color4b): RectStyle = RectStyle(colors = RectColors.solid(color))

        @JvmStatic
        fun rounded(color: Color4b, radius: Float): RectStyle = RectStyle(
            colors = RectColors.solid(color),
            corners = RectCorners.all(radius)
        )

        @JvmStatic
        fun gradient(
            topLeft: Color4b,
            topRight: Color4b,
            bottomRight: Color4b,
            bottomLeft: Color4b
        ): RectStyle = RectStyle(colors = RectColors(topLeft, topRight, bottomRight, bottomLeft))

        @JvmStatic
        fun textured(
            textureId: Int,
            tint: Color4b = Color4b.WHITE,
            mix: Float = 0f,
            alpha: Float = 1f,
            uv: RectUv = RectUv.DEFAULT
        ): RectStyle = RectStyle(
            colors = RectColors.solid(tint),
            textureId = textureId,
            mix = mix,
            alpha = alpha,
            uv = uv,
            type = RectType.TEXTURE
        )
    }
}
