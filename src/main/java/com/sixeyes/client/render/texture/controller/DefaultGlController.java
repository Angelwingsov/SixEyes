package com.sixeyes.client.render.texture.controller;

import org.lwjgl.opengl.GL11;
import java.nio.ByteBuffer;


public class DefaultGlController implements GlController {
    public static final int GL_RGB = 6407;
    public static final int GL_RGBA = 6408;

    @Override
    public void run(Runnable runnable) {
        runnable.run();
    }

    @Override
    public int genTexId() {
        return GL11.glGenTextures();
    }

    @Override
    public void deleteTexture(int id) {
        GL11.glDeleteTextures(id);
    }

    @Override
    public void bindTexture(int id) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
    }

    @Override
    public void texParameter(int target, int pname, int param) {
        GL11.glTexParameteri(target, pname, param);
    }

    @Override
    public void texParameter(int target, int pname, float param) {
        GL11.glTexParameterf(target, pname, param);
    }

    @Override
    public void texImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
    }

    @Override
    public void texSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, long pixels) {
        GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
    }

    @Override
    public void pixelStore(int pname, int param) {
        GL11.glPixelStorei(pname, param);
    }
}


