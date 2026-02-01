package com.sixeyes.client.api.render.renderers;

import com.sixeyes.client.render.comet.vertex.DrawMode;
import com.sixeyes.client.render.comet.vertex.format.VertexFormat;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.GlTextureView;
import com.sixeyes.client.api.render.Batchable;
import com.sixeyes.client.api.render.RenderPipeline;
import com.sixeyes.client.api.render.RenderUtil;
import com.sixeyes.client.api.render.Renderable;

import java.awt.*;
public class BlurRectRenderer extends Renderable implements Batchable {
    private final TextureRectRenderer rectRenderer = new TextureRectRenderer();

    @Override
    public String name() {
        return "blur-rect";
    }

    @Override
    public String shader() {
        return rectRenderer.shader();
    }

    @Override
    public DrawMode drawMode() {
        return rectRenderer.drawMode();
    }

    @Override
    public VertexFormat vertexFormat() {
        return rectRenderer.vertexFormat();
    }

    @Override
    public Snippets snippets() {
        return rectRenderer.snippets();
    }

    @Override
    public void load() {
        rectRenderer.load();
    }

    public BlurRectRenderer priority(RenderPipeline pipeline) {
        rectRenderer.priority(pipeline);
        return this;
    }

    public void draw(float x, float y, float width, float height, Color color, float radius, float mix) {
        Framebuffer fbo = RenderUtil.KAWASE.getFBO();
        GlTextureView texture = RenderUtil.KAWASE.getTexture();

        float u, u1, v, v1;

        if (mix != 1) {
            int texW = fbo.textureWidth;
            int texH = fbo.textureHeight;

            int fbw = mc.getWindow().getFramebufferWidth();

            float xScale = (float) fbw / mc.getWindow().getScaledWidth();

            float fbX = x * xScale;
            float fbY = y * xScale;
            float fbW = width * xScale;
            float fbH = height * xScale;

            u = fbX / texW;
            u1 = (fbX + fbW) / texW;

            v = 1f - (fbY + fbH) / texH;
            v1 = 1f - fbY / texH;
        } else {
            u = u1 = v = v1 = 0f;
        }

        rectRenderer.draw(
                x, y, width, height, color, texture.texture().getGlId(),
                radius, mix,
                u, v, u1 - u, v1 - v
        );
    }

    @Override
    public void begin() {
        rectRenderer.begin();
    }

    @Override
    public void end() {
        rectRenderer.end();
    }

    @Override
    public void flush(RenderPipeline pipeline) {
        rectRenderer.flush(pipeline);
    }
}


