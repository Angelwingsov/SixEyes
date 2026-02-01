package sweetie.evaware.api.render;

import com.ferra13671.cometrenderer.CometLoaders;
import com.ferra13671.cometrenderer.CometRenderer;
import com.ferra13671.cometrenderer.builders.GlProgramBuilder;
import com.ferra13671.cometrenderer.program.GlProgram;
import com.ferra13671.cometrenderer.program.GlProgramSnippet;
import com.ferra13671.cometrenderer.program.shader.ShaderType;
import com.ferra13671.cometrenderer.vertex.DrawMode;
import com.ferra13671.cometrenderer.vertex.format.VertexFormat;
import com.ferra13671.cometrenderer.vertex.mesh.IMesh;
import sweetie.evaware.Mc;

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
