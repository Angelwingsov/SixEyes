package com.sixeyes.client.render.texture.loader;

import com.sixeyes.client.render.texture.builder.GLTextureBuilder;
import com.sixeyes.client.render.texture.texture.ColorMode;
import com.sixeyes.client.render.texture.texture.TextureFiltering;
import com.sixeyes.client.render.texture.texture.TextureWrapping;
import com.sixeyes.client.render.texture.builder.GLTextureInfo;


public abstract class TextureLoader<T> {

    public abstract GLTextureInfo load(T path, ColorMode colorMode, TextureFiltering filtering, TextureWrapping wrapping) throws Exception;

    public GLTextureBuilder<T> createTextureBuilder() {
        return new GLTextureBuilder<>(this);
    }
}


