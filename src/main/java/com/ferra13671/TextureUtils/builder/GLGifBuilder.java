package com.ferra13671.TextureUtils.builder;

import com.ferra13671.TextureUtils.gif.DecompileMode;
import com.ferra13671.TextureUtils.gif.GLGif;
import com.ferra13671.TextureUtils.loader.GifLoader;


public class GLGifBuilder {
    private String name = null;
    private GifLoader loader = null;
    private DecompileMode decompileMode;

    private GLGifBuilder() {}

    public GLGifBuilder name(String name) {
        this.name = name;
        return this;
    }

    public GLGifBuilder loader(GifLoader loader) {
        this.loader = loader;
        return this;
    }

    public GLGifBuilder decompileMode(DecompileMode decompileMode) {
        this.decompileMode = decompileMode;
        return this;
    }

    public GLGif build() {
        try {
            checkArguments();
            return GLGif.of(name, loader.load(decompileMode));
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private void checkArguments() {
        if (name == null)
            throw new IllegalArgumentException("Name cannot be null.");
        if (loader == null)
            throw new IllegalArgumentException(String.format("Loader in gif '%s' cannot be null.", name));
        if (decompileMode == null)
            throw new IllegalArgumentException(String.format("DecompileMode in gif '%s' cannot be null.", name));
    }

    public static GLGifBuilder builder() {
        return new GLGifBuilder();
    }
}
