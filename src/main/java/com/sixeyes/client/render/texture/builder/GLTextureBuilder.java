package com.sixeyes.client.render.texture.builder;

import com.sixeyes.client.render.texture.loader.TextureLoader;
import com.sixeyes.client.render.texture.texture.ColorMode;
import com.sixeyes.client.render.texture.texture.GLTexture;
import com.sixeyes.client.render.texture.texture.TextureFiltering;
import com.sixeyes.client.render.texture.texture.TextureWrapping;


public class GLTextureBuilder<T> {
    private String name = null;
    private T path = null;
    private final TextureLoader<T> loader;
    private ColorMode colorMode = ColorMode.RGBA;
    private TextureFiltering filtering = null;
    private TextureWrapping wrapping = null;

    public GLTextureBuilder(TextureLoader<T> loader) {
        this.loader = loader;
    }

    public GLTextureBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public GLTextureBuilder<T> path(T path) {
        this.path = path;
        return this;
    }

    public GLTextureBuilder<T> colorMode(ColorMode colorMode) {
        this.colorMode = colorMode;
        return this;
    }

    public GLTextureBuilder<T> filtering(TextureFiltering filtering) {
        this.filtering = filtering;
        return this;
    }

    public GLTextureBuilder<T> wrapping(TextureWrapping wrapping) {
        this.wrapping = wrapping;
        return this;
    }

    public GLTexture build() {
        try {
            checkArguments();
            return GLTexture.of(name, loader.load(path, colorMode, filtering, wrapping));
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private void checkArguments() {
        if (name == null)
            throw new IllegalArgumentException("Name cannot be null.");
        if (loader == null)
            throw new IllegalArgumentException(String.format("Loader in texture '%s' cannot be null.", name));
        if (path == null)
            throw new IllegalArgumentException(String.format("Path in texture '%s' cannot be null.", name));
        if (colorMode == null)
            throw new IllegalArgumentException(String.format("ColorMode in texture '%s' cannot be null.", name));
        if (filtering == null)
            throw new IllegalArgumentException(String.format("TextureFiltering in texture '%s' cannot be null.", name));
        if (wrapping == null)
            throw new IllegalArgumentException(String.format("TextureWrapping in texture '%s' cannot be null.", name));
    }
}
