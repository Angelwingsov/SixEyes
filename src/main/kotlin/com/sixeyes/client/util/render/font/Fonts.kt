package com.sixeyes.client.util.render.font

import com.sixeyes.client.extensions.CLIENT_ID
import java.util.concurrent.ConcurrentHashMap

/**
 * Исправленный менеджер шрифтов.
 * Автоматически добавляет 'assets/sixeyes/' к путям, чтобы FontLoader мог их найти.
 */
object Fonts {

    private val fontMap = ConcurrentHashMap<String, Font>()

    /**
     * Основной метод получения шрифта.
     * Теперь он корректно склеивает полный путь к ассету.
     */
    @JvmStatic
    fun get(name: String, path: String): Font {
        return fontMap.getOrPut(name) {
            // Формируем полный путь: assets/sixeyes/font/g_sans/google_sans_regular
            val fullPath = "assets/$CLIENT_ID/$path"

            FontLoader.load(
                name = name,
                jsonPath = "$fullPath.json",
                pngPath = "$fullPath.png"
            )
        }
    }

    // --- Свойства с путями относительно папки ассетов твоего мода ---

    @JvmStatic val google_sans_regular: Font get() = get("google_sans_regular", "font/g_sans/google_sans_regular")
    @JvmStatic val google_sans_medium: Font get() = get("google_sans_medium", "font/g_sans/google_sans_medium")
    @JvmStatic val google_sans_semi: Font get() = get("google_sans_semi", "font/g_sans/google_sans_semi")
    @JvmStatic val google_sans_bold: Font get() = get("google_sans_bold", "font/g_sans/google_sans_bold")

    @JvmStatic val google_sans_flex: Font get() = google_sans_regular

    @JvmStatic val product_sans_black: Font get() = get("product_sans_black", "font/p_sans/product_sans_black")
    @JvmStatic val product_sans_bold: Font get() = get("product_sans_bold", "font/p_sans/product_sans_bold")
    @JvmStatic val product_sans_medium: Font get() = get("product_sans_medium", "font/p_sans/product_sans_medium")
    @JvmStatic val product_sans_regular: Font get() = get("product_sans_regular", "font/p_sans/product_sans_regular")
    @JvmStatic val product_sans_light: Font get() = get("product_sans_light", "font/p_sans/product_sans_light")
    @JvmStatic val product_sans_thin: Font get() = get("product_sans_thin", "font/p_sans/product_sans_thin")

    /**
     * Предзагрузка основных шрифтов.
     */
    @JvmStatic
    fun preload() {
        google_sans_regular
        product_sans_regular
    }
}