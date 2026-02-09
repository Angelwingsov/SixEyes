package com.sixeyes.client.render.texture.texture;

import org.lwjgl.opengl.GL11;


public enum TextureFiltering {
    DEFAULT(GL11.GL_NEAREST),
    SMOOTH(GL11.GL_LINEAR);

    public final int id;

    TextureFiltering(int id) {
        this.id = id;
    }
}
