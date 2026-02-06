package com.sixeyes.client.render.comet.buffer.framebuffer;

import com.sixeyes.client.render.comet.FrameBufferUtils;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.GlTexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.awt.*;

public class CometFrameBuffer extends Framebuffer {
    private static final int TRANSLUCENT = new Color(0f, 0f, 0f, 0f).hashCode();
    private final int clearColor;

    public CometFrameBuffer(String name, boolean useDepth) {
        this(name, 1, 1, useDepth);
    }

    public CometFrameBuffer(String name, int width, int height, boolean useDepth) {
        this(name, width, height, TRANSLUCENT, useDepth);
    }

    public CometFrameBuffer(String name, int width, int height, int clearColor, boolean useDepth) {
        super(name, useDepth);
        this.resize(width, height);
        this.clearColor = clearColor;
    }

    
    public void clearColorTexture() {
        if (this.colorAttachment != null)
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(this.colorAttachment, this.clearColor);
    }

    
    public void clearDepthTexture() {
        if (this.depthAttachment != null)
            RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(this.depthAttachment, 0f);
    }

    
    public void clearAllTextures() {
        clearColorTexture();
        clearDepthTexture();
    }

    public void setFilter(FilterMode filter) {
        if (!(this.colorAttachment instanceof GlTexture glTexture)) {
            return;
        }

        int target = (glTexture.usage() & GpuTexture.USAGE_CUBEMAP_COMPATIBLE) != 0
                ? GL13.GL_TEXTURE_CUBE_MAP
                : GlConst.GL_TEXTURE_2D;
        int glFilter = filter == FilterMode.LINEAR ? GL11.GL_LINEAR : GL11.GL_NEAREST;

        GL11.glBindTexture(target, glTexture.getGlId());
        GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, glFilter);
        GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, glFilter);
    }

    
    public void bind(boolean setViewport) {
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, FrameBufferUtils.getFrameBufferId(this.colorAttachmentView, this.depthAttachmentView));

        if (setViewport)
            GlStateManager._viewport(0, 0, this.colorAttachment.getWidth(0), this.colorAttachment.getHeight(0));
    }
}


