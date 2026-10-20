package com.sixeyes.client.render.main;

import com.mojang.renderpearl.backend.opengl.DirectStateAccess;
import com.mojang.renderpearl.backend.opengl.FrameBufferAttachment;
import com.mojang.renderpearl.backend.opengl.FrameBufferCache;
import com.mojang.renderpearl.backend.opengl.GlConst;
import com.mojang.renderpearl.backend.opengl.GlDevice;
import com.mojang.renderpearl.backend.opengl.GlStateManager;
import com.mojang.renderpearl.frontend.FrontendGpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.GpuTextureView;

import java.util.Collections;

public final class FrameBufferUtils {
    public static void bindFrameBuffer(int id, GpuTextureView colorTexture) {
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, id);

        GlStateManager._viewport(0, 0, colorTexture.getWidth(0), colorTexture.getHeight(0));
    }

    public static int getFrameBufferId(GpuTextureView colorTexture, GpuTextureView depthTexture) {
        validateFrameBufferTexture("Color", colorTexture);

        if (depthTexture != null)
            validateFrameBufferTexture("Depth", depthTexture);

        GlDevice glDevice = (GlDevice) ((FrontendGpuDevice) RenderSystem.getDevice()).backend;
        DirectStateAccess dsa = glDevice.directStateAccess();
        FrameBufferCache cache = glDevice.frameBufferCache();
        FrameBufferAttachment color = (FrameBufferAttachment) colorTexture.texture();
        FrameBufferAttachment depth = depthTexture == null ? null : (FrameBufferAttachment) depthTexture.texture();
        return cache.getFbo(dsa, Collections.singletonList(color), depth);
    }

    public static void validateFrameBufferTexture(String name, GpuTextureView gpuTextureView) {
        if (gpuTextureView.isClosed())
            throw new IllegalStateException(name.concat("texture is closed"));

        if ((gpuTextureView.texture().usage() & 8) == 0)
            throw new IllegalStateException(name.concat("texture must have USAGE_RENDER_ATTACHMENT"));

        if (gpuTextureView.texture().getDepthOrLayers() > 1)
            throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported as an attachment");
    }
}
