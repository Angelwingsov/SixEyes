package sweetie.evaware.api.render.renderers;

import com.ferra13671.cometrenderer.CometRenderer;
import com.ferra13671.cometrenderer.vertex.DrawMode;
import com.ferra13671.cometrenderer.vertex.element.VertexElementType;
import com.ferra13671.cometrenderer.vertex.format.VertexFormat;
import com.ferra13671.cometrenderer.vertex.mesh.IMesh;
import com.ferra13671.cometrenderer.vertex.mesh.Mesh;
import com.ferra13671.cometrenderer.vertex.mesh.MeshBuilder;
import sweetie.evaware.api.render.Batchable;
import sweetie.evaware.api.render.RenderPipeline;
import sweetie.evaware.api.render.Renderable;

import java.awt.*;

public class ShadowRenderer extends Renderable implements Batchable {
    private final MeshBuilder[] batchBuilders = new MeshBuilder[RenderPipeline.VALUES.length];
    private boolean isBatching = false;
    private RenderPipeline currentPipeline = RenderPipeline.GAME;

    @Override
    public String name() {
        return "shadow";
    }

    @Override
    public String shader() {
        return "shade/shadow";
    }

    @Override
    public DrawMode drawMode() {
        return DrawMode.QUADS;
    }

    @Override
    public VertexFormat vertexFormat() {
        return VertexFormat.builder()
                .element("Color", VertexElementType.FLOAT, 4)
                .element("Size", VertexElementType.FLOAT, 2)
                .element("Radius", VertexElementType.FLOAT, 4)
                .element("ShadowSize", VertexElementType.FLOAT, 1)
                .element("Settings", VertexElementType.FLOAT, 2) // x=Fill, y=Inner
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

    public ShadowRenderer priority(RenderPipeline pipeline) {
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

    @Override
    public void end() {
        isBatching = false;
    }

    public void draw(float x, float y, float width, float height, Color color, float radius, float shadowSize, boolean fill, boolean shadowIn) {
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;

        float fFill = fill ? 1.0f : 0.0f;
        float fInner = shadowIn ? 1.0f : 0.0f;

        MeshBuilder currentBuilder;
        if (isBatching) {
            currentBuilder = batchBuilders[currentPipeline.ordinal()];
        } else {
            currentBuilder = Mesh.builder(drawMode(), vertexFormat());
        }

        if (!shadowIn) {
            x -= shadowSize;
            y -= shadowSize;
            width += shadowSize * 2f;
            height += shadowSize * 2f;
        }

        // TL
        currentBuilder.vertex(x, y, 0)
                .element("Color", VertexElementType.FLOAT, r, g, b, a)
                .element("Size", VertexElementType.FLOAT, width, height)
                .element("Radius", VertexElementType.FLOAT, radius, radius, radius, radius)
                .element("ShadowSize", VertexElementType.FLOAT, shadowSize)
                .element("Settings", VertexElementType.FLOAT, fFill, fInner);

        // BL
        currentBuilder.vertex(x, y + height, 0)
                .element("Color", VertexElementType.FLOAT, r, g, b, a)
                .element("Size", VertexElementType.FLOAT, width, height)
                .element("Radius", VertexElementType.FLOAT, radius, radius, radius, radius)
                .element("ShadowSize", VertexElementType.FLOAT, shadowSize)
                .element("Settings", VertexElementType.FLOAT, fFill, fInner);

        // BR
        currentBuilder.vertex(x + width, y + height, 0)
                .element("Color", VertexElementType.FLOAT, r, g, b, a)
                .element("Size", VertexElementType.FLOAT, width, height)
                .element("Radius", VertexElementType.FLOAT, radius, radius, radius, radius)
                .element("ShadowSize", VertexElementType.FLOAT, shadowSize)
                .element("Settings", VertexElementType.FLOAT, fFill, fInner);

        // TR
        currentBuilder.vertex(x + width, y, 0)
                .element("Color", VertexElementType.FLOAT, r, g, b, a)
                .element("Size", VertexElementType.FLOAT, width, height)
                .element("Radius", VertexElementType.FLOAT, radius, radius, radius, radius)
                .element("ShadowSize", VertexElementType.FLOAT, shadowSize)
                .element("Settings", VertexElementType.FLOAT, fFill, fInner);


        if (!isBatching) {
            drawImmediate(currentBuilder.buildNullable(), false, true);
        } else {
            currentPipeline = RenderPipeline.GAME;
        }
    }

    public void drawShadow(float x, float y, float width, float height, Color color, float radius, float shadowSize) {
        draw(x, y, width, height, color, radius, shadowSize, false, false);
    }

    public void drawInnerShadow(float x, float y, float width, float height, Color color, float radius, float shadowSize) {
        draw(x, y, width, height, color, radius, shadowSize, false, true);
    }

    public void drawFilled(float x, float y, float width, float height, Color color, float radius, float shadowSize) {
        draw(x, y, width, height, color, radius, shadowSize, true, false);
    }
}