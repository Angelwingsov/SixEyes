package com.sixeyes.client.api.render;

import com.sixeyes.client.render.comet.CometLoaders;
import com.sixeyes.client.render.comet.CometRenderer;
import com.sixeyes.client.render.comet.builders.GlProgramBuilder;
import com.sixeyes.client.render.comet.program.GlProgram;
import com.sixeyes.client.render.comet.program.GlProgramSnippet;
import com.sixeyes.client.render.comet.program.shader.ShaderType;
import com.sixeyes.client.render.comet.vertex.DrawMode;
import com.sixeyes.client.render.comet.vertex.format.VertexFormat;
import com.sixeyes.client.render.comet.vertex.mesh.IMesh;
import com.sixeyes.client.Mc;

public abstract class Renderable implements Mc {
    public record Snippets(GlProgramSnippet... snippets) {}

    private static final String SHADER_PATH = "assets/evaware/shaders/";
    public static final String SHADER_CORE_PATH = SHADER_PATH + "core/";
    public static final String SHADER_INCLUDE_PATH = SHADER_PATH + "include/";

    public abstract String name();
    public abstract String shader();

    public abstract DrawMode drawMode();
    public abstract VertexFormat vertexFormat();

    public abstract Snippets snippets();
    public GlProgram glProgram;

    public void load() {

    }

    public GlProgramBuilder<?> createShaderBuilder(
            String name,
            String fsh,
            String vsh,
            Snippets snippets
    ) {
        return CometLoaders.IN_JAR.createProgramBuilder(snippets.snippets())
                .name(name)
                .shader(CometLoaders.IN_JAR.createGlslFileEntry(name + "-vertex", SHADER_CORE_PATH + vsh + ".vsh"), ShaderType.Vertex)
                .shader(CometLoaders.IN_JAR.createGlslFileEntry(name + "-fragment", SHADER_CORE_PATH + fsh + ".fsh"), ShaderType.Fragment);
    }

    public void initMatrix() {
        CometRenderer.initMatrix();
    }

    public void initColor() {
        CometRenderer.initShaderColor();
    }

    public void resetColor() {
        CometRenderer.resetShaderColor();
    }

    public void setGlobalProgram(GlProgram globalProgram) {
        CometRenderer.setGlobalProgram(globalProgram);
    }

    protected void drawImmediate(IMesh mesh, boolean color, boolean matrix) {
        setGlobalProgram(glProgram);

        if (matrix) initMatrix();
        if (color) {
            initColor();
            CometRenderer.draw(mesh);
            resetColor();
        } else {
            CometRenderer.draw(mesh);
        }
    }

    protected void drawImmediate(IMesh mesh) {
        drawImmediate(mesh, true, true);
    }
}


