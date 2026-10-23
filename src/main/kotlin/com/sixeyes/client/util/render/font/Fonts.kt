package com.sixeyes.client.util.render.font

import com.sixeyes.client.extensions.CLIENT_ID
import java.util.concurrent.ConcurrentHashMap

object Fonts {
    private val map = ConcurrentHashMap<String, Font>()

    @JvmStatic
    fun get(name: String, path: String): Font = map.computeIfAbsent(name) {
        val full = "assets/$CLIENT_ID/$path"
        FontLoader.load(name, "$full.json", "$full.png")
    }

    @JvmStatic val google_sans_regular: Font by lazy { get("google_sans_regular", "font/g_sans/google_sans_regular") }
    @JvmStatic val google_sans_medium: Font by lazy { get("google_sans_medium", "font/g_sans/google_sans_medium") }
    @JvmStatic val google_sans_semi: Font by lazy { get("google_sans_semi", "font/g_sans/google_sans_semi") }
    @JvmStatic val google_sans_bold: Font by lazy { get("google_sans_bold", "font/g_sans/google_sans_bold") }
    @JvmStatic val google_sans_flex: Font get() = google_sans_regular

    @JvmStatic val product_sans_black: Font by lazy { get("product_sans_black", "font/p_sans/product_sans_black") }
    @JvmStatic val product_sans_bold: Font by lazy { get("product_sans_bold", "font/p_sans/product_sans_bold") }
    @JvmStatic val product_sans_medium: Font by lazy { get("product_sans_medium", "font/p_sans/product_sans_medium") }
    @JvmStatic val product_sans_regular: Font by lazy { get("product_sans_regular", "font/p_sans/product_sans_regular") }
    @JvmStatic val product_sans_light: Font by lazy { get("product_sans_light", "font/p_sans/product_sans_light") }
    @JvmStatic val product_sans_thin: Font by lazy { get("product_sans_thin", "font/p_sans/product_sans_thin") }

    @JvmStatic val default: Font get() = google_sans_regular

    @JvmStatic
    fun preload() {
        google_sans_regular
        google_sans_medium
        product_sans_regular
    }
}