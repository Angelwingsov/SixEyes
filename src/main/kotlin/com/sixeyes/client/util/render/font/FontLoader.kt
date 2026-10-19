package com.sixeyes.client.util.render.font

import com.google.gson.JsonParser
import com.sixeyes.client.render.texture.loader.TextureLoaders
import com.sixeyes.client.render.texture.texture.ColorMode
import com.sixeyes.client.render.texture.texture.GLTexture
import com.sixeyes.client.render.texture.texture.TextureFiltering
import com.sixeyes.client.render.texture.texture.TextureWrapping
import com.sixeyes.client.util.other.fromAssets
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object FontLoader {
    fun load(name: String, jsonPath: String, pngPath: String): Font {
        val json = fromAssets(jsonPath)?.use { s ->
            InputStreamReader(s, StandardCharsets.UTF_8).use { r ->
                JsonParser.parseReader(r).asJsonObject
            }
        } ?: error("Failed to load font json: $jsonPath")

        val atlas = json.getAsJsonObject("atlas")
        val metrics = json.getAsJsonObject("metrics")
        val w = atlas.get("width").asFloat
        val h = atlas.get("height").asFloat
        val range = atlas.get("distanceRange")?.asFloat ?: 8f
        val bottomOrigin = atlas.get("yOrigin")?.asString.equals("bottom", ignoreCase = true)

        val invW = 1f / w
        val invH = 1f / h

        val ascii = arrayOfNulls<Font.Glyph>(128)
        val map = Int2ObjectOpenHashMap<Font.Glyph>()

        val glyphs = json.getAsJsonArray("glyphs")
        if (glyphs != null) {
            for (e in glyphs) {
                val obj = e.asJsonObject
                val code = obj.get("unicode").asInt
                val adv = obj.get("advance").asFloat

                val plane = obj.getAsJsonObject("planeBounds")
                val bounds = obj.getAsJsonObject("atlasBounds")

                val g = if (plane != null && bounds != null) {
                    val u0 = bounds.get("left").asFloat * invW
                    val u1 = bounds.get("right").asFloat * invW
                    val v0 = if (bottomOrigin) 1f - bounds.get("top").asFloat * invH else bounds.get("top").asFloat * invH
                    val v1 = if (bottomOrigin) 1f - bounds.get("bottom").asFloat * invH else bounds.get("bottom").asFloat * invH
                    Font.Glyph(
                        adv,
                        u0, v0, u1, v1,
                        plane.get("left").asFloat,
                        plane.get("top").asFloat,
                        plane.get("right").asFloat,
                        plane.get("bottom").asFloat,
                        false
                    )
                } else {
                    Font.Glyph(adv, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, true)
                }

                if (code < 128) {
                    ascii[code] = g
                } else {
                    map.put(code, g)
                }
            }
        }

        val texStream = fromAssets(pngPath) ?: error("Font texture not found: $pngPath")
        val texInfo = texStream.use {
            TextureLoaders.INPUT_STREAM.load(it, ColorMode.RGBA, TextureFiltering.SMOOTH, TextureWrapping.DEFAULT)
        } ?: error("Failed to decode font texture: $pngPath")

        val texture = GLTexture.of(name, texInfo)
        val fallback = ascii['?'.code] ?: map.get(65533) ?: ascii[' '.code]

        return Font(
            name = name,
            lineHeight = metrics.get("lineHeight").asFloat,
            ascender = metrics.get("ascender").asFloat,
            descender = metrics.get("descender").asFloat,
            ascii = ascii,
            glyphs = map,
            fallback = fallback,
            batch = MsdfBatchState(texture, range)
        )
    }
}