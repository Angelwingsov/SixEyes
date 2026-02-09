package com.sixeyes.client.util.render

import com.sixeyes.client.extensions.CLIENT_ID
import com.sixeyes.client.util.other.fromAssets
import com.sixeyes.client.render.texture.TextureStorage
import com.sixeyes.client.render.texture.loader.TextureLoaders
import com.sixeyes.client.render.texture.texture.ColorMode
import com.sixeyes.client.render.texture.texture.GLTexture
import com.sixeyes.client.render.texture.texture.TextureFiltering
import com.sixeyes.client.render.texture.texture.TextureWrapping
import java.io.InputStream

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

        println("Starting texture loading...")
        val start = System.currentTimeMillis()

        loadQueue.forEach { (name, path) ->
            loadSingle(name, path)
        }

        loadQueue.clear()
        bootstrapped = true

        println("Loaded textures in ${System.currentTimeMillis() - start} ms")
    }

    private fun loadSingle(name: String, path: String) {
        val resourcePath = "assets/$CLIENT_ID/$path"

        val stream: InputStream? = fromAssets(resourcePath)

        if (stream == null) {
            System.err.println("Texture file not found: $path")
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

                val ok = TextureStorage.addTexture(name, texture)
                println("${if (ok) "[SUCCESS]" else "[FAILED]"} Loaded texture: $name")
            }
        } catch (e: Exception) {
            System.err.println("Failed to load texture: $name")
            e.printStackTrace()
        }
    }

    fun get(name: String): GLTexture? {
        return TextureStorage.getTexture(name)
    }
}
