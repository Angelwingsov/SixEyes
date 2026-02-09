package com.sixeyes.client.render.texture;


public interface GlTex {

    void delete();

    void bind();

    void unBind();

    int getWidth();

    int getHeight();

    int getTexId();
}
