package com.sixeyes.client.util.other

import com.sixeyes.SixEyes
import java.io.InputStream

fun fromAssets(path: String): InputStream? {
    return SixEyes::class.java.classLoader.getResourceAsStream(path);
}