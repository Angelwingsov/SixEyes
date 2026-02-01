package com.sixeyes.client.render.texture.builder;

import com.sixeyes.client.render.texture.texture.ColorMode;
import com.sixeyes.client.render.texture.texture.TextureFiltering;
import com.sixeyes.client.render.texture.texture.TextureWrapping;

import java.nio.ByteBuffer;


public class GLTextureInfo {
    private final ByteBuffer pixels;
    private final int width, height;
    private final ColorMode colorMode;
    private final TextureFiltering filtering;
    private final TextureWrapping wrapping;
    private final boolean usingStb;

    public GLTextureInfo(ByteBuffer pixels, int width, int height, ColorMode colorMode, TextureFiltering filtering, TextureWrapping wrapping, boolean usingStb) {
        this.pixels = pixels;
        this.width = width;
        this.height = height;
        this.colorMode = colorMode;
        this.filtering = filtering;
        this.wrapping = wrapping;
        this.usingStb = usingStb;
    }

    public ByteBuffer getPixels() {
        return pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ColorMode getColorMode() {
        return colorMode;
    }

    public TextureFiltering getFiltering() {
        return filtering;
    }

    public TextureWrapping getWrapping() {
        return wrapping;
    }

    public boolean isUsingStb() {
        return usingStb;
    }
}


