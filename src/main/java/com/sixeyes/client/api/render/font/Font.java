package com.sixeyes.client.api.render.font;

import com.sixeyes.client.render.comet.CometLoaders;
import com.sixeyes.client.render.comet.CometRenderer;
import com.sixeyes.client.render.comet.Pair;
import com.sixeyes.client.render.comet.program.GlProgram;
import com.sixeyes.client.render.comet.program.shader.ShaderType;
import com.sixeyes.client.render.comet.program.uniform.UniformType;
import com.sixeyes.client.render.comet.vertex.DrawMode;
import com.sixeyes.client.render.comet.vertex.element.VertexElementType;
import com.sixeyes.client.render.comet.vertex.format.VertexFormat;
import com.sixeyes.client.render.comet.vertex.mesh.IMesh;
import com.sixeyes.client.render.comet.vertex.mesh.Mesh;
import com.sixeyes.client.render.comet.vertex.mesh.MeshBuilder;
import lombok.Getter;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTextureView;
import net.minecraft.text.Text;
import com.sixeyes.client.api.render.Batchable;
import com.sixeyes.client.api.render.RenderPipeline;
import com.sixeyes.client.api.render.font.FontData.AtlasData;
import com.sixeyes.client.api.render.font.FontData.MetricsData;
import com.sixeyes.client.helpers.ReplaceUtil;
import com.sixeyes.client.helpers.TextUtil;

import java.awt.*;
import java.util.List;
import java.util.Map;

import static com.sixeyes.client.api.render.Renderable.SHADER_CORE_PATH;

public final class Font implements Batchable {
    private static GlProgram glProgram;

    private static final VertexFormat FONT_FORMAT = VertexFormat.builder()
            .element("Texture", VertexElementType.FLOAT, 2)
            .element("Color", VertexElementType.FLOAT, 4)
            .element("Style", VertexElementType.FLOAT, 3)
            .element("OutlineColor", VertexElementType.FLOAT, 4)
            .build();

    private final String name;
    private final AbstractTexture texture;
    @Getter private final AtlasData atlas;
    @Getter private final MetricsData metrics;
    private final Map<Integer, MsdfGlyph> glyphs;
    private final Map<Integer, Map<Integer, Float>> kernings;

    private final MeshBuilder[] batchBuilders = new MeshBuilder[RenderPipeline.VALUES.length];
    private boolean isBatching = false;
    private RenderPipeline currentPipeline = RenderPipeline.GAME;

    public Font(String name, AbstractTexture texture, AtlasData atlas, MetricsData metrics, Map<Integer, MsdfGlyph> glyphs, Map<Integer, Map<Integer, Float>> kernings) {
        this.name = name;
        this.texture = texture;
        this.atlas = atlas;
        this.metrics = metrics;
        this.glyphs = glyphs;
        this.kernings = kernings;
    }

    public Font priority(RenderPipeline pipeline) {
        this.currentPipeline = pipeline;
        return this;
    }

    @Override
    public void begin() {
        isBatching = true;
        for (int i = 0; i < batchBuilders.length; i++) {
            if (batchBuilders[i] == null) {
                batchBuilders[i] = Mesh.builder(DrawMode.QUADS, FONT_FORMAT);
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
            drawMeshImmediate(mesh);
        }

        batchBuilders[index] = Mesh.builder(DrawMode.QUADS, FONT_FORMAT);
    }

    @Override
    public void end() {
        isBatching = false;
    }

    public void drawText(Text text, float x, float y, float size, float thickness, float smoothness, float spacing, int outlineColor, float outlineThickness) {
        if (text == null) return;

        MeshBuilder builder = getBuilder();

        applyGlyphs(builder, TextUtil.parseTextToColoredGlyphs(ReplaceUtil.replaceSymbols(text)),
                size, (thickness + outlineThickness * 0.5f) * 0.5f * size, spacing, x, y + getMetrics().baselineHeight() * size, 0f,
                thickness, smoothness, outlineThickness, outlineColor);

        processDraw(builder);
    }

    public void drawText(String text, float x, float y, float size, float thickness, int color, int colorSecond, float offset, float smoothness, float spacing, int outlineColor, float outlineThickness) {
        MeshBuilder builder = getBuilder();

        applyGlyphs(builder, text,
                size, (thickness + outlineThickness * 0.5f) * 0.5f * size, spacing, x, y + getMetrics().baselineHeight() * size, 0f,
                color, colorSecond, offset, thickness, smoothness, outlineThickness, outlineColor);

        processDraw(builder);
    }

    private MeshBuilder getBuilder() {
        if (isBatching) {
            return batchBuilders[currentPipeline.ordinal()];
        } else {
            return Mesh.builder(DrawMode.QUADS, FONT_FORMAT);
        }
    }

    private void processDraw(MeshBuilder builder) {
        if (!isBatching) {
            drawMeshImmediate(builder.buildNullable());
        } else {
            currentPipeline = RenderPipeline.GAME;
        }
    }

    private void drawMeshImmediate(IMesh mesh) {
        if (mesh == null) return;
        if (glProgram == null) initProgram();

        glProgram.getUniform("uRange", UniformType.FLOAT).set(getAtlas().range());
        glProgram.getUniform("uTexture", UniformType.SAMPLER).set(((GlTextureView) texture.getGlTextureView()));

        CometRenderer.setGlobalProgram(glProgram);
        CometRenderer.initMatrix();

        CometRenderer.draw(mesh);
    }

    private void initProgram() {
        glProgram = CometLoaders.IN_JAR.createProgramBuilder(CometRenderer.getMatrixSnippet())
                .name("text")
                .shader(CometLoaders.IN_JAR.createGlslFileEntry("text-vertex", SHADER_CORE_PATH + "font/text.vsh"), ShaderType.Vertex)
                .shader(CometLoaders.IN_JAR.createGlslFileEntry("text-fragment", SHADER_CORE_PATH + "font/text.fsh"), ShaderType.Fragment)
                .uniform("uRange", UniformType.FLOAT)
                .sampler("uTexture")
                .build();
    }

    public void applyGlyphs(MeshBuilder builder, String text, float size, float thickness, float spacing, float x, float y, float z, int color, int colorSecond, float offset,
                            float th, float sm, float outTh, int outCol) {
        float outR = (outCol >> 16 & 0xFF) / 255f;
        float outG = (outCol >> 8 & 0xFF) / 255f;
        float outB = (outCol & 0xFF) / 255f;
        float outA = (outCol >> 24 & 0xFF) / 255f;

        int prevChar = -1;
        for (int i = 0; i < text.length(); i++) {
            int _char = text.charAt(i);
            MsdfGlyph glyph = this.glyphs.get(_char);

            if (glyph == null) continue;

            Map<Integer, Float> kerning = this.kernings.get(prevChar);
            if (kerning != null) {
                x += kerning.getOrDefault(_char, 0.0f) * size;
            }

            x += glyph.apply(builder, x, y, z, size, color, th, sm, outTh, outR, outG, outB, outA) + thickness + spacing;
            prevChar = _char;
        }
    }

    public void applyGlyphs(MeshBuilder builder, List<MsdfGlyph.ColoredGlyph> glyphs, float size, float thickness, float spacing, float x, float y, float z,
                            float th, float sm, float outTh, int outCol) {

        float outR = (outCol >> 16 & 0xFF) / 255f;
        float outG = (outCol >> 8 & 0xFF) / 255f;
        float outB = (outCol & 0xFF) / 255f;
        float outA = (outCol >> 24 & 0xFF) / 255f;

        int prevChar = -1;
        for (MsdfGlyph.ColoredGlyph glyphData : glyphs) {
            int _char = glyphData.c();
            int color = glyphData.color();

            MsdfGlyph glyph = this.glyphs.get(_char);
            if (glyph == null) continue;

            Map<Integer, Float> kerning = this.kernings.get(prevChar);
            if (kerning != null) {
                x += kerning.getOrDefault(_char, 0.0f) * size;
            }

            x += glyph.apply(builder, x, y, z, size, color, th, sm, outTh, outR, outG, outB, outA) + thickness + spacing;
            prevChar = _char;
        }
    }

    private Pair<Float, Float> offset(float x, float y) {
        float scale = 1f; float x1 = x; float y1 = y;
        boolean isPS = name.contains(Fonts.ps); boolean isSF = name.contains(Fonts.sf);
        if (isSF || isPS) { y1 -= scale; if (isPS) x1 -= scale / 2f; }
        return new Pair<>(x1, y1);
    }

    public void drawText(Text text, float x, float y, float size, float thickness) {
        Pair<Float, Float> coordinates = offset(x, y);
        drawText(text, coordinates.left(), coordinates.right(), size, thickness, 0.5f, 0f, -1, thickness);
    }

    public void drawText(String text, float x, float y, float size, Color color, float thickness) {
        Pair<Float, Float> coordinates = offset(x, y);
        drawText(text, coordinates.left(), coordinates.right(), size, thickness, color.getRGB(), -1, -1f, 0.5f, 0f, -1, thickness);
    }

    public void drawGradientText(String text, float x, float y, float size, Color colorFirst, Color colorSecond, float offset, float thickness) {
        Pair<Float, Float> coordinates = offset(x, y);
        drawText(text, coordinates.left(), coordinates.right(), size, thickness, colorFirst.getRGB(), colorSecond.getRGB(), offset, 0.5f, 0f, -1, thickness);
    }

    public void drawGradientText(String text, float x, float y, float size, Color color, Color colorSecond, float offset) {
        drawGradientText(text, x, y, size, color, colorSecond, offset, 0f);
    }

    public void drawText(Text text, float x, float y, float size) {
        drawText(text, x, y, size, 0f);
    }

    public void drawText(String text, float x, float y, float size, Color color) {
        drawText(text, x, y, size, color, 0f);
    }

    public void drawCenteredText(String text, float x, float y, float size, Color color, float thickness) {
        drawText(text, x - getWidth(text, size, thickness) / 2f, y, size, color, thickness);
    }

    public void drawCenteredText(String text, float x, float y, float size, Color color) {
        drawCenteredText(text, x, y, size, color, 0f);
    }

    public void drawCenteredGradientText(String text, float x, float y, float size, Color color, Color colorSecond, float offset, float thickness) {
        drawGradientText(text, x - getWidth(text, size, thickness) / 2f, y, size, color, colorSecond, offset, thickness);
    }

    public void drawCenteredGradientText(String text, float x, float y, float size, Color color, Color colorSecond, float offset) {
        drawCenteredGradientText(text, x, y, size, color, colorSecond, offset, 0f);
    }

    public float getHeight(float size) { return size; }
    public float getWidth(Text text, float size) { return getWidth(text, size, 0f); }
    public float getWidth(String text, float size) { return getWidth(text, size, 0f); }

    public float getWidth(Text text, float size, float thickness) {
        if (text == null) return 0f;
        List<MsdfGlyph.ColoredGlyph> glyphs = TextUtil.parseTextToColoredGlyphs(text);
        int prevChar = -1; float width = 0.0f;
        for (MsdfGlyph.ColoredGlyph glyphData : glyphs) {
            int _char = glyphData.c();
            MsdfGlyph glyph = this.glyphs.get(_char);
            if (glyph == null) continue;
            Map<Integer, Float> kerning = this.kernings.get(prevChar);
            if (kerning != null) width += kerning.getOrDefault(_char, 0.0f) * size * (1f + thickness);
            width += glyph.getWidth(size);
            prevChar = _char;
        }
        return width;
    }

    public float getWidth(String text, float size, float thickness) {
        int prevChar = -1; float width = 0.0f;
        for (int i = 0; i < text.length(); i++) {
            int _char = text.charAt(i);
            MsdfGlyph glyph = this.glyphs.get(_char);
            if (glyph == null) continue;
            Map<Integer, Float> kerning = this.kernings.get(prevChar);
            if (kerning != null) width += kerning.getOrDefault(_char, 0.0f) * size * (1f + thickness);
            width += glyph.getWidth(size) * (1f + thickness);
            prevChar = _char;
        }
        return width;
    }

    public static FontBuilder builder() {
        return new FontBuilder();
    }
}

