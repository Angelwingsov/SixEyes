package com.sixeyes.client.api.render.renderers;

import com.sixeyes.client.render.comet.*;
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

public class RectRenderer extends Renderable implements Batchable {
    private final MeshBuilder[] batchBuilders = new MeshBuilder[RenderPipeline.VALUES.length];
    private boolean isBatching = false;

    private RenderPipeline currentPipeline = RenderPipeline.GAME;

    @Override
    public String name() { return "rect"; }

    @Override
    public String shader() { return "rect/rect"; }

    @Override
    public DrawMode drawMode() { return DrawMode.QUADS; }

    @Override
    public VertexFormat vertexFormat() {
        return VertexFormat.builder()
                .element("Color", VertexElementType.FLOAT, 4)
                .element("Size", VertexElementType.FLOAT, 2)
                .element("Radius", VertexElementType.FLOAT, 4)
                .build();
    }

    @Override
    public Snippets snippets() {
        return new Snippets(CometRenderer.getMatrixSnippet());
    }

    @Override
    public void load() {
        if (glProgram == null) {
            glProgram = createShaderBuilder(name(), shader(), shader(), snippets()).build();
        }
    }

    public RectRenderer priority(RenderPipeline pipeline) {
        this.currentPipeline = pipeline;
        return this;
    }

    @Override
    public void begin() {
        isBatching = true;
        for (int i = 0; i < batchBuilders.length; i++) {
            if (batchBuilders[i] == null) {
                batchBuilders[i] = Mesh.builder(drawMode(), vertexFormat());
            }
        }
        currentPipeline = RenderPipeline.GAME;
    }

    @Override
    public void end() {
        isBatching = false;
    }

    @Override
    public void flush(RenderPipeline pipeline) {
        if (!isBatching) return;

        int index = pipeline.ordinal();
        MeshBuilder builder = batchBuilders[index];

        if (builder == null) return;

        IMesh mesh = builder.buildNullable();
        if (mesh != null) {
            drawImmediate(mesh, false, true);
        }

        batchBuilders[index] = Mesh.builder(drawMode(), vertexFormat());
    }

    public void draw(float x, float y, float width, float height, Color color, float radius) {
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;

        MeshBuilder currentBuilder;
        if (isBatching) {
            currentBuilder = batchBuilders[currentPipeline.ordinal()];
        } else {
            currentBuilder = Mesh.builder(drawMode(), vertexFormat());
        }

        currentBuilder.vertex(x, y, 0)
                .element("Color", VertexElementType.FLOAT, r, g, b, a)
                .element("Size", VertexElementType.FLOAT, width, height)
                .element("Radius", VertexElementType.FLOAT, radius, radius, radius, radius);

        currentBuilder.vertex(x, y + height, 0)
                .element("Color", VertexElementType.FLOAT, r, g, b, a)
                .element("Size", VertexElementType.FLOAT, width, height)
                .element("Radius", VertexElementType.FLOAT, radius, radius, radius, radius);

        currentBuilder.vertex(x + width, y + height, 0)
                .element("Color", VertexElementType.FLOAT, r, g, b, a)
                .element("Size", VertexElementType.FLOAT, width, height)
                .element("Radius", VertexElementType.FLOAT, radius, radius, radius, radius);

        currentBuilder.vertex(x + width, y, 0)
                .element("Color", VertexElementType.FLOAT, r, g, b, a)
                .element("Size", VertexElementType.FLOAT, width, height)
                .element("Radius", VertexElementType.FLOAT, radius, radius, radius, radius);

        if (!isBatching) {
            drawImmediate(currentBuilder.buildNullable(), false, true);
        }

        if (isBatching) {
            currentPipeline = RenderPipeline.GAME;
        }
    }
}

