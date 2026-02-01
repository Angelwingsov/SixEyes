package com.sixeyes.client.render.texture;

import com.sixeyes.client.render.texture.controller.DefaultGlController;
import com.sixeyes.client.render.texture.controller.GlController;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class GLTextureSystem {
    protected static final List<GlTex> ALL_TEXTURES = new CopyOnWriteArrayList<>();

    protected static GlController glController = new DefaultGlController();

    public static void setGlController(GlController controller) {
        glController = controller;
    }

    public static GlController getGlController() {
        return glController;
    }

    public static void addTexture(GlTex texture) {
        ALL_TEXTURES.add(texture);
    }

    public static void removeTexture(GlTex texture) {
        ALL_TEXTURES.remove(texture);
    }

    public static void close() {
        ALL_TEXTURES.forEach(GlTex::delete);
        ALL_TEXTURES.clear();
    }
}


