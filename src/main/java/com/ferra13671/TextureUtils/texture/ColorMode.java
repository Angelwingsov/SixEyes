package com.ferra13671.TextureUtils.texture;

import com.ferra13671.TextureUtils.controller.DefaultGlController;


public enum ColorMode {
    RGB(DefaultGlController.GL_RGB),
    RGBA(DefaultGlController.GL_RGBA);

    public final int glId;

    ColorMode(int glId) {
        this.glId = glId;
    }
}