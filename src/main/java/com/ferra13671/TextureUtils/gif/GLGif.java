package com.ferra13671.TextureUtils.gif;

import com.ferra13671.TextureUtils.*;
import com.ferra13671.TextureUtils.builder.GLGifInfo;
import com.ferra13671.TextureUtils.texture.GLTexture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GLGif implements GlTex {
    private final String name;
    private int updateDelayMillis;
    private long lastTime;
    private final List<GLTexture> textures = new ArrayList<>();
    private Iterator<GLTexture> iterator;
    private GLTexture currentTexture;

    protected GLGif(String name) {
        this.name = name;
    }

    protected GLGif create(GLGifInfo info) {
        lastTime = System.currentTimeMillis();
        updateDelayMillis = info.getDelay();

        for (int i = 0; i < info.getTextures().size(); i++)
            textures.add(GLTexture.of(name.concat("_").concat(String.valueOf(i)), info.getTextures().get(i)));
        iterator = textures.iterator();

        return this;
    }

    public void update() {
        if (System.currentTimeMillis() - lastTime >= updateDelayMillis) {
            if (!iterator.hasNext()) iterator = textures.iterator();
            currentTexture = iterator.next();
            lastTime = System.currentTimeMillis();
        }
    }

    @Override
    public void delete() {
        for (GLTexture glTexture : textures) {
            glTexture.delete();
        }
        textures.clear();
    }

    @Override
    public void bind() {
        currentTexture.bind();
    }

    @Override
    public void unBind() {
        currentTexture.unBind();
    }

    @Override
    public int getTexId() {
        return currentTexture.getTexId();
    }

    public String getName() {
        return name;
    }

    @Override
    public int getWidth() {
        return currentTexture.getWidth();
    }

    @Override
    public int getHeight() {
        return currentTexture.getHeight();
    }

    public int getUpdateDelayMillis() {
        return updateDelayMillis;
    }

    public void setUpdateDelayMillis(int updateDelayMillis) {
        this.updateDelayMillis = updateDelayMillis;
    }

    public static GLGif of(String name, GLGifInfo info) {
        return new GLGif(name).create(info);
    }
}
