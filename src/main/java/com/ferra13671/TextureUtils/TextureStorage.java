package com.ferra13671.TextureUtils;

import com.ferra13671.TextureUtils.texture.GLTexture;

import java.util.HashMap;
import java.util.Map;


public class TextureStorage {
    private static final Map<String, GLTexture> TEXTURES = new HashMap<>();

    public static boolean addTexture(String key, GLTexture texture) {
        if (TEXTURES.containsKey(key)) return false;
        TEXTURES.put(key, texture);
        return true;
    }

    public static boolean removeTexture(String key) {
        if (!TEXTURES.containsKey(key)) return false;
        TEXTURES.remove(key);
        return true;
    }

    public static GLTexture getTexture(String key) {
        return TEXTURES.get(key);
    }

    private static GLTexture register(String key, GLTexture texture) {
        TEXTURES.put(key, texture);
        return texture;
    }
}
