package com.sixeyes.client.util.render

import com.sixeyes.client.extensions.CLIENT_ID
import com.sixeyes.client.extensions.LOGGER
import com.sixeyes.client.render.texture.TextureStorage
import com.sixeyes.client.render.texture.loader.TextureLoaders
import com.sixeyes.client.render.texture.texture.ColorMode
import com.sixeyes.client.render.texture.texture.GLTexture
import com.sixeyes.client.render.texture.texture.TextureFiltering
import com.sixeyes.client.render.texture.texture.TextureWrapping
import com.sixeyes.client.util.other.fromAssets

object TextureLoader {
    private val loadQueue: MutableMap<String, String> = HashMap()
    private var bootstrapped = false

    fun register(name: String, path: String) {
        if (bootstrapped) {
            loadSingle(name, path)
        } else {
            loadQueue[name] = path
        }
    }

    fun load() {
        if (bootstrapped) return

        val start = System.currentTimeMillis()
        loadQueue.forEach { (name, path) ->
            loadSingle(name, path)
        }
        loadQueue.clear()
        bootstrapped = true

        LOGGER.info("Loaded textures in {} ms", System.currentTimeMillis() - start)
    }

    private fun loadSingle(name: String, path: String) {
        val resourcePath = "assets/$CLIENT_ID/$path"
        val stream = fromAssets(resourcePath)

        if (stream == null) {
            LOGGER.warn("Texture file not found: {}", path)
            return
        }

        try {
            stream.use {
                val info = TextureLoaders.INPUT_STREAM.load(
                    it,
                    ColorMode.RGBA,
                    TextureFiltering.SMOOTH,
                    TextureWrapping.DEFAULT
                )
                val texture = GLTexture.of(name, info)
                TextureStorage.addTexture(name, texture)
            }
        } catch (e: Exception) {
            LOGGER.error("Failed to load texture: {}", name, e)
        }
    }

    fun get(name: String): GLTexture? = TextureStorage.getTexture(name)
}
