package com.sixeyes.client.render.texture.texture;

import com.sixeyes.client.render.texture.controller.DefaultGlController;


public enum ColorMode {
    RGB(DefaultGlController.GL_RGB),
    RGBA(DefaultGlController.GL_RGBA);

    public final int glId;

    ColorMode(int glId) {
        this.glId = glId;
    }
}

