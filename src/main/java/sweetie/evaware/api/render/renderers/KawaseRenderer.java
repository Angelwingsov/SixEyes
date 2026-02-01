package sweetie.evaware.api.render.renderers;

import com.ferra13671.cometrenderer.*;
import com.ferra13671.cometrenderer.buffer.framebuffer.CometFrameBuffer;
import com.ferra13671.cometrenderer.program.GlProgram;
import com.ferra13671.cometrenderer.program.uniform.UniformType;
import com.ferra13671.cometrenderer.vertex.DrawMode;
import com.ferra13671.cometrenderer.vertex.format.VertexFormat;
import com.ferra13671.cometrenderer.vertex.mesh.IMesh;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.GlTextureView;
import org.joml.Vector2f;
import sweetie.evaware.api.render.Renderable;

import java.util.ArrayList;
import java.util.List;

public class KawaseRenderer extends Renderable {
    private GlProgram downscaleProgram;
    private GlProgram upscaleProgram;

    private final List<CometFrameBuffer> fbos = new ArrayList<>();
    private final int blurPasses = 3;
    private final float offset = 20.0f;

    private boolean init = false;

    @Override
    public String name() {
        return "kawase";
    }

    @Override
    public String shader() {
        return "kawase/";
    }

    @Override
    public DrawMode drawMode() {
        return DrawMode.QUADS;
    }

    @Override
    public VertexFormat vertexFormat() {
        return CometVertexFormats.POSITION;
    }

    @Override
    public Snippets snippets() {
        return new Snippets(CometRenderer.getMatrixSnippet());
    }

    @Override
    public void load() {
        if (!init) {
            createPrograms();
            createFramebuffers();
            init = true;
        }
    }

    private void createPrograms() {
        downscaleProgram = createShaderBuilder("downscale", shader() + "downscale", "pos", snippets())
                .uniform("uHalfTexelSize", UniformType.VEC2)
                .uniform("uOffset", UniformType.FLOAT)
                .sampler("uTexture")
                .build();

        upscaleProgram = createShaderBuilder("upscale", shader() + "upscale", "pos", snippets())
                .uniform("uHalfTexelSize", UniformType.VEC2)
                .uniform("uOffset", UniformType.FLOAT)
                .sampler("uTexture")
                .build();

        glProgram = downscaleProgram;
    }

    private void checkResize() {
        int w = mc.getWindow().getFramebufferWidth();
        int h = mc.getWindow().getFramebufferHeight();

        if (fbos.isEmpty() || fbos.getFirst().textureWidth != w || fbos.getFirst().textureHeight != h) {
            for (Framebuffer fbo : fbos) {
                fbo.delete();
            }
            fbos.clear();
            createFramebuffers();
        }
    }

    private void createFramebuffers() {
        int width = mc.getWindow().getFramebufferWidth();
        int height = mc.getWindow().getFramebufferHeight();

        for (int i = 0; i <= blurPasses; i++) {
            var h = new CometFrameBuffer("kawase_fbo_" + i, width, height, false);
            h.setFilter(FilterMode.LINEAR);
            fbos.add(h);
        }
    }

    public void applyBlur() {
        checkResize();

        if (fbos.isEmpty()) return;

        int actualPasses = Math.max(fbos.size() - 1, 1);

        applyBlurPass(downscaleProgram, mc.getFramebuffer(), fbos.getFirst(), 0);

        for (int i = 0; i < actualPasses; i++) {
            applyBlurPass(downscaleProgram, fbos.get(i), fbos.get(i + 1), i + 1);
        }

        for (int i = actualPasses; i > 0; i--) {
            applyBlurPass(upscaleProgram, fbos.get(i), fbos.get(i - 1), i);
        }
    }

    private void applyBlurPass(GlProgram program, Framebuffer source, CometFrameBuffer destination, int pass) {
        CometRenderer.disableBlend();
        destination.clearAllTextures();
        CometRenderer.bindFramebuffer(destination);
        setGlobalProgram(program);
        initMatrix();

        AddressMode prevAddressModeU = source.getColorAttachment().addressModeU;
        AddressMode prevAddressModeV = source.getColorAttachment().addressModeV;

        float texelSizeX = 1f / source.textureWidth;
        float texelSizeY = 1f / source.textureHeight;

        program.getUniform("uHalfTexelSize", UniformType.VEC2).set(new Vector2f(texelSizeX, texelSizeY));
        program.getUniform("uOffset", UniformType.FLOAT).set((offset * 0.5f) * (pass / (float) blurPasses));

        program.getUniform("uTexture", UniformType.SAMPLER).set((GlTextureView) source.getColorAttachmentView());

        source.getColorAttachment().setAddressMode(AddressMode.CLAMP_TO_EDGE);
        drawFullscreenQuad();
        source.getColorAttachment().setAddressMode(prevAddressModeU, prevAddressModeV);
    }

    private void drawFullscreenQuad() {
        float width = mc.getWindow().getScaledWidth();
        float height = mc.getWindow().getScaledHeight();

        IMesh mesh = CometRenderer.createMesh(drawMode(), vertexFormat(), buffer -> {
            buffer.vertex(0, 0, 0);
            buffer.vertex(0, height, 0);
            buffer.vertex(width, height, 0);
            buffer.vertex(width, 0, 0);
        });

        CometRenderer.draw(mesh);
    }

    public Framebuffer getFBO() {
        return fbos.getFirst();
    }

    public GlTextureView getTexture() {
        return (GlTextureView) fbos.getFirst().getColorAttachmentView();
    }
}