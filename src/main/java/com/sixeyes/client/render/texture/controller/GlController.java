package com.sixeyes.client.render.texture.controller;

import java.nio.ByteBuffer;


public interface GlController {

    void run(Runnable runnable);

    int genTexId();

    void deleteTexture(int id);

    void bindTexture(int id);

    void texParameter(int target, int pname, int param);

    void texParameter(int target, int pname, float param);

    void texImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels);

    void texSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, long pixels);

    void pixelStore(int pname, int param);
}


