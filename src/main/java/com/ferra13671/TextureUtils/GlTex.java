package com.ferra13671.TextureUtils;


public interface GlTex {

    void delete();

    void bind();

    void unBind();

    int getWidth();

    int getHeight();

    int getTexId();
}
