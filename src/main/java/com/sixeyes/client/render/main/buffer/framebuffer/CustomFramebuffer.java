package com.sixeyes.client.render.main.buffer.framebuffer;

import com.sixeyes.client.render.main.FrameBufferUtils;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.*;

public class CustomFramebuffer extends RenderTarget {
    private static final int TRANSLUCENT = new Color(0f, 0f, 0f, 0f).hashCode();
    private final int clearColor;

    public CustomFramebuffer(String name, boolean useDepth) {
        this(name, 1, 1, useDepth);
    }

    public CustomFramebuffer(String name, int width, int height, boolean useDepth) {
        this(name, width, height, TRANSLUCENT, useDepth);
    }

    public CustomFramebuffer(String name, int width, int height, int clearColor, boolean useDepth) {
        super(name, useDepth);
        this.resize(width, height);
        this.clearColor = clearColor;
    }

    public void clearColorTexture() {
        if (this.colorTexture != null)
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(this.colorTexture, this.clearColor);
    }

    public void clearDepthTexture() {
        if (this.depthTexture != null)
            RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(this.depthTexture, 0f);
    }

    public void clearAllTextures() {
        clearColorTexture();
        clearDepthTexture();
    }
    
    public void bind(boolean setViewport) {
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, FrameBufferUtils.getFrameBufferId(this.colorTextureView, this.depthTextureView));

        if (setViewport)
            GlStateManager._viewport(0, 0, this.colorTexture.getWidth(0), this.colorTexture.getHeight(0));
    }
}
