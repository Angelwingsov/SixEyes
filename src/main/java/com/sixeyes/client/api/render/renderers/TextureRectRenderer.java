package com.sixeyes.client.api.render.renderers;

import com.sixeyes.client.render.comet.CometRenderer;
import com.sixeyes.client.render.comet.program.uniform.UniformType;
import com.sixeyes.client.render.comet.vertex.DrawMode;
import com.sixeyes.client.render.comet.vertex.element.VertexElementType;
import com.sixeyes.client.render.comet.vertex.format.VertexFormat;
import com.sixeyes.client.render.comet.vertex.mesh.IMesh;
import com.sixeyes.client.render.comet.vertex.mesh.Mesh;
import com.sixeyes.client.render.comet.vertex.mesh.MeshBuilder;
import com.sixeyes.client.api.render.Batchable;
import com.sixeyes.client.api.render.RenderPipeline;
import com.sixeyes.client.api.render.Renderable;

import java.awt.*;

public class TextureRectRenderer extends Renderable implements Batchable {
    private final MeshBuilder[] builders = new MeshBuilder[RenderPipeline.VALUES.length];
    private final int[] lastTextureIds = new int[RenderPipeline.VALUES.length];
    private RenderPipeline currentPipeline = RenderPipeline.GAME;
    private boolean isBatching = false;

    @Override
    public String name() {
        return "texture-rect";
    }

    @Override
    public String shader() {
        return "texture/rect_texture";
    }

    @Override
    public DrawMode drawMode() {
        return DrawMode.QUADS;
    }

    @Override
    public VertexFormat vertexFormat() {
        return VertexFormat.builder()
                .element("Color", VertexElementType.FLOAT, 4)
                .element("Texture", VertexElementType.FLOAT, 2)
                .element("Size", VertexElementType.FLOAT, 2)
                .element("Radius", VertexElementType.FLOAT, 4)
                .element("Mix", VertexElementType.FLOAT, 1)
                .build();
    }

    @Override
    public Snippets snippets() {
        return new Snippets(CometRenderer.getMatrixSnippet());
    }

    @Override
    public void load() {
        if (glProgram == null) {
            glProgram = createShaderBuilder(name(), shader(), shader(), snippets())
                    .sampler("uTexture")
                    .build();
        }
    }

    @Override
    public void begin() {
        isBatching = true;
        for(int i=0; i<builders.length; i++) {
            if(builders[i] == null) builders[i] = Mesh.builder(drawMode(), vertexFormat());
            lastTextureIds[i] = -1;
        }
        currentPipeline = RenderPipeline.GAME;
    }

    @Override
    public void flush(RenderPipeline pipeline) {
        if (!isBatching) return;

        int idx = pipeline.ordinal();
        MeshBuilder builder = builders[idx];

        IMesh mesh = builder.buildNullable();
        if (mesh != null) {
            glProgram.getUniform("uTexture", UniformType.SAMPLER).set(lastTextureIds[idx]);
            drawImmediate(mesh, false, true);
        }
        builders[idx] = Mesh.builder(drawMode(), vertexFormat());
        lastTextureIds[idx] = -1;
    }

    @Override
    public void end() {
        isBatching = false;
    }

    public TextureRectRenderer priority(RenderPipeline pipeline) {
        this.currentPipeline = pipeline;
        return this;
    }

    private void flushInternal(int layerIndex) {
        MeshBuilder builder = builders[layerIndex];
        IMesh mesh = builder.buildNullable();
        if (mesh != null) {
            glProgram.getUniform("uTexture", UniformType.SAMPLER).set(lastTextureIds[layerIndex]);
            drawImmediate(mesh, false, true);
        }
        builders[layerIndex] = Mesh.builder(drawMode(), vertexFormat());
    }

    public void draw(float x, float y, float width, float height, Color color, int texture,
                     float radius, float mix,
                     float u, float v, float textureWidth, float textureHeight) {

        float u2 = u + textureWidth;
        float v2 = v + textureHeight;

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;

        int idx = currentPipeline.ordinal();

        if (lastTextureIds[idx] != -1 && lastTextureIds[idx] != texture) {
            flushInternal(idx);
        }
        lastTextureIds[idx] = texture;

        MeshBuilder currentBuilder = isBatching ? builders[idx] : Mesh.builder(drawMode(), vertexFormat());

        currentBuilder.vertex(x, y, 0)
                .element("Color", VertexElementType.FLOAT, r, g, b, a)
                .element("Texture", VertexElementType.FLOAT, u, v2)
                .element("Size", VertexElementType.FLOAT, width, height)
                .element("Radius", VertexElementType.FLOAT, radius, radius, radius, radius)
                .element("Mix", VertexElementType.FLOAT, mix);

        currentBuilder.vertex(x, y + height, 0)
                .element("Color", VertexElementType.FLOAT, r, g, b, a)
                .element("Texture", VertexElementType.FLOAT, u, v)
                .element("Size", VertexElementType.FLOAT, width, height)
                .element("Radius", VertexElementType.FLOAT, radius, radius, radius, radius)
                .element("Mix", VertexElementType.FLOAT, mix);

        currentBuilder.vertex(x + width, y + height, 0)
                .element("Color", VertexElementType.FLOAT, r, g, b, a)
                .element("Texture", VertexElementType.FLOAT, u2, v)
                .element("Size", VertexElementType.FLOAT, width, height)
                .element("Radius", VertexElementType.FLOAT, radius, radius, radius, radius)
                .element("Mix", VertexElementType.FLOAT, mix);

        currentBuilder.vertex(x + width, y, 0)
                .element("Color", VertexElementType.FLOAT, r, g, b, a)
                .element("Texture", VertexElementType.FLOAT, u2, v2)
                .element("Size", VertexElementType.FLOAT, width, height)
                .element("Radius", VertexElementType.FLOAT, radius, radius, radius, radius)
                .element("Mix", VertexElementType.FLOAT, mix);

        if (!isBatching) {
            glProgram.getUniform("uTexture", UniformType.SAMPLER).set(texture);
            drawImmediate(currentBuilder.buildNullable(), false, true);
        }

        if (isBatching) {
            currentPipeline = RenderPipeline.GAME;
        }
    }
}

