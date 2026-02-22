package com.sixeyes.client.util.render.font

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.sixeyes.client.render.texture.texture.ColorMode
import com.sixeyes.client.render.texture.texture.GLTexture
import com.sixeyes.client.render.texture.texture.TextureFiltering
import com.sixeyes.client.render.texture.texture.TextureWrapping
import com.sixeyes.client.render.texture.loader.TextureLoaders
import com.sixeyes.client.util.other.fromAssets
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object FontLoader {

    fun load(name: String, jsonPath: String, pngPath: String): Font {
        val root = parseJson(jsonPath) ?: error("Failed to load font JSON: $jsonPath")

        val atlas = root.getAsJsonObject("atlas") ?: error("Missing 'atlas' object in $jsonPath")
        val metrics = root.getAsJsonObject("metrics") ?: error("Missing 'metrics' object in $jsonPath")

        val atlasWidth = atlas.get("width").asFloat
        val atlasHeight = atlas.get("height").asFloat
        val atlasBottomOrigin = atlas.get("yOrigin")?.asString?.equals("bottom", true) ?: true
        val distanceRange = atlas.get("distanceRange").asFloat

        val lineHeight = metrics.get("lineHeight").asFloat
        val ascender = metrics.get("ascender").asFloat

        val glyphMap = parseGlyphs(root)
        val fallback = glyphMap['?'.code] ?: glyphMap[65533]

        val texture = loadTexture(name, pngPath)

        return Font(
            atlasWidth = atlasWidth,
            atlasHeight = atlasHeight,
            atlasBottomOrigin = atlasBottomOrigin,
            lineHeight = lineHeight,
            ascender = ascender,
            glyphs = glyphMap,
            fallbackGlyph = fallback,
            batchState = MsdfBatchState(texture, distanceRange)
        )
    }

    private fun parseJson(path: String): JsonObject? {
        return fromAssets(path)?.use { stream ->
            InputStreamReader(stream, StandardCharsets.UTF_8).use { reader ->
                JsonParser.parseReader(reader).asJsonObject
            }
        }
    }

    private fun parseGlyphs(root: JsonObject): Map<Int, Font.Glyph> {
        val glyphsArray = root.getAsJsonArray("glyphs") ?: return emptyMap()
        val glyphMap = HashMap<Int, Font.Glyph>(glyphsArray.size())

        for (element in glyphsArray) {
            val obj = element.asJsonObject
            val unicode = obj.get("unicode").asInt

            glyphMap[unicode] = Font.Glyph(
                advance = obj.get("advance").asFloat,
                plane = obj.getAsJsonObject("planeBounds")?.toPlaneBounds(),
                atlas = obj.getAsJsonObject("atlasBounds")?.toAtlasBounds()
            )
        }
        return glyphMap
    }

    private fun loadTexture(name: String, path: String): GLTexture {
        val stream = fromAssets(path) ?: error("Font texture not found: $path")
        return stream.use {
            val info = TextureLoaders.INPUT_STREAM.load(
                it, ColorMode.RGBA, TextureFiltering.SMOOTH, TextureWrapping.DEFAULT
            ) ?: error("Failed to decode font texture: $path")
            GLTexture.of(name, info)
        }
    }

    private fun JsonObject.toPlaneBounds() = Font.PlaneBounds(
        left = get("left").asFloat,
        bottom = get("bottom").asFloat,
        right = get("right").asFloat,
        top = get("top").asFloat
    )

    private fun JsonObject.toAtlasBounds() = Font.AtlasBounds(
        left = get("left").asFloat,
        bottom = get("bottom").asFloat,
        right = get("right").asFloat,
        top = get("top").asFloat
    )
}