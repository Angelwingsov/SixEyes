package com.sixeyes.client.render.texture.builder;

import java.util.List;


public class GLGifInfo {
    private final List<GLTextureInfo> textures;
    private final int delay;

    public GLGifInfo(List<GLTextureInfo> textures, int delay) {
        this.textures = textures;
        this.delay = delay;
    }

    public List<GLTextureInfo> getTextures() {
        return textures;
    }

    public int getDelay() {
        return delay;
    }
}


