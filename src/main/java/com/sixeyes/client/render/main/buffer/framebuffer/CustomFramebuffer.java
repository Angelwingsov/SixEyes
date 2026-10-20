package com.sixeyes.client.render.main.buffer.framebuffer;

import com.sixeyes.client.render.main.FrameBufferUtils;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.backend.opengl.GlConst;
import com.mojang.renderpearl.backend.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector4f;
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
        super(name, GpuFormat.RGBA8_UNORM, useDepth ? GpuFormat.D32_FLOAT : null);
        this.resize(width, height);
        this.clearColor = clearColor;
    }

    public void clearColorTexture() {
        if (this.colorTexture != null) {
            float r = ((clearColor >> 16) & 0xFF) / 255.0f;
            float g = ((clearColor >> 8) & 0xFF) / 255.0f;
            float b = (clearColor & 0xFF) / 255.0f;
            float a = ((clearColor >> 24) & 0xFF) / 255.0f;
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(this.colorTexture, new Vector4f(r, g, b, a));
        }
    }

    public void clearDepthTexture() {
        if (this.depthTexture != null)
            RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(this.depthTexture, 0.0);
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
