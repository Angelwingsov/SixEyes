package com.sixeyes.client.util.render.font

import com.mojang.blaze3d.platform.NativeImage
import com.sixeyes.client.util.other.fromAssets
import com.sixeyes.client.util.render.LinearDynamicTexture
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier
import java.util.concurrent.ConcurrentHashMap

object VulkanFontAtlas {
    private val textureCache = ConcurrentHashMap<String, Identifier>()

    fun getOrRegister(fontName: String, pngPath: String): Identifier {
        return textureCache.computeIfAbsent(fontName) {
            val safeName = fontName.lowercase().replace('-', '_')
            val identifier = Identifier.fromNamespaceAndPath("sixeyes", "textures/font/$safeName.png")

            val stream = fromAssets(pngPath) ?: error("Font texture not found for Vulkan: $pngPath")
            val image = stream.use { NativeImage.read(it) }

            val dynamicTexture = LinearDynamicTexture({ "sixeyes_font_$safeName" }, image)
            Minecraft.getInstance().textureManager.register(identifier, dynamicTexture)
            identifier
        }
    }
}
