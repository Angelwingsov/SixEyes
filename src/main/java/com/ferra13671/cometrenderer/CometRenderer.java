package com.ferra13671.cometrenderer;

import com.ferra13671.cometrenderer.blend.DstFactor;
import com.ferra13671.cometrenderer.blend.SrcFactor;
import com.ferra13671.cometrenderer.program.GlProgram;
import com.ferra13671.cometrenderer.program.GlProgramSnippet;
import com.ferra13671.cometrenderer.program.uniform.UniformType;
import com.ferra13671.cometrenderer.program.uniform.uniforms.Matrix4fGlUniform;
import com.ferra13671.cometrenderer.program.uniform.uniforms.Vec4GlUniform;
import com.ferra13671.cometrenderer.program.uniform.uniforms.buffer.BufferUniform;
import com.ferra13671.cometrenderer.scissor.ScissorStack;
import com.ferra13671.cometrenderer.vertex.DrawMode;
import com.ferra13671.cometrenderer.vertex.format.VertexFormat;
import com.ferra13671.cometrenderer.vertex.mesh.IMesh;
import com.ferra13671.cometrenderer.vertex.mesh.Mesh;
import com.ferra13671.cometrenderer.vertex.mesh.MeshBuilder;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlGpuBuffer;
import net.minecraft.client.render.BuiltBuffer;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CometRenderer {
    
    private static boolean initialized = false;
    
    private static final Logger logger = LoggerFactory.getLogger("CometRenderer");
    
    private static Function<GlGpuBuffer, Integer> bufferIdGetter;
    
    private static Supplier<Integer> scaleGetter;
    
    private static Vector4f shaderColor = new Vector4f(1f, 1f, 1f, 1f);
    
    private static final GlProgramSnippet matrixSnippet = CometLoaders.IN_JAR.createProgramBuilder()
            .uniform("Projection", UniformType.BUFFER)
            .uniform("modelViewMat", UniformType.MATRIX)
            .buildSnippet();
    
    private static final GlProgramSnippet colorSnippet = CometLoaders.IN_JAR.createProgramBuilder()
            .uniform("shaderColor", UniformType.VEC4)
            .buildSnippet();
    
    private static GlProgram globalProgram;
    
    private static final ScissorStack scissorStack = new ScissorStack();

    
    public static void init(Function<GlGpuBuffer, Integer> bufferIdGetter, Supplier<Integer> scaleGetter) {

        if (initialized)
            throw new IllegalStateException("CometRenderer has already initialized.");



        CometRenderer.bufferIdGetter = bufferIdGetter;

        CometRenderer.scaleGetter = scaleGetter;

        initialized = true;
    }

    
    public static Logger getLogger() {
        return logger;
    }

    
    public static Function<GlGpuBuffer, Integer> getBufferIdGetter() {
        return bufferIdGetter;
    }

    
    public static Supplier<Integer> getScaleGetter() {
        return scaleGetter;
    }

    
    public static Vector4f getShaderColor() {
        return shaderColor;
    }

    
    public static void setShaderColor(Vector4f shaderColor) {
        CometRenderer.shaderColor = shaderColor;
    }

    
    public static void resetShaderColor() {
        setShaderColor(new Vector4f(1f, 1f, 1f, 1f));
    }

    
    public static GlProgramSnippet getMatrixSnippet() {
        return matrixSnippet;
    }

    
    public static GlProgramSnippet getColorSnippet() {
        return colorSnippet;
    }

    
    public static void initMatrix() {
        BufferUniform projectionUniform = globalProgram.getUniform("Projection", UniformType.BUFFER);
        if (projectionUniform != null) {
            GpuBufferSlice slice = RenderSystem.getProjectionMatrixBuffer();
            projectionUniform.set(slice);
        }

        Matrix4fGlUniform modelViewUniform = globalProgram.getUniform("modelViewMat", UniformType.MATRIX);
        if (modelViewUniform != null)
            modelViewUniform.set(RenderSystem.getModelViewMatrix());
    }

    
    public static void initShaderColor() {
        Vec4GlUniform colorUniform = globalProgram.getUniform("shaderColor", UniformType.VEC4);
        if (colorUniform != null)
            colorUniform.set(getShaderColor());
    }

    
    public static void setGlobalProgram(GlProgram globalProgram) {
        CometRenderer.globalProgram = globalProgram;
    }

    
    public static GlProgram getGlobalProgram() {
        return globalProgram;
    }

    
    public static ScissorStack getScissorStack() {
        return scissorStack;
    }

    
    public static void applyDefaultBlend() {
        GlStateManager._enableBlend();
        GL11.glBlendFunc(SrcFactor.SRC_ALPHA.glId, DstFactor.ONE_MINUS_SRC_ALPHA.glId);
    }

    
    public static void applyBlend(SrcFactor srcFactor, DstFactor dstFactor) {
        GlStateManager._enableBlend();
        GL11.glBlendFunc(srcFactor.glId, dstFactor.glId);
    }

    
    public static void applyBlend(SrcFactor srcColor, DstFactor dstColor, SrcFactor srcAlpha, DstFactor dstAlpha) {
        GlStateManager._enableBlend();
        GL14.glBlendFuncSeparate(srcColor.glId, dstColor.glId, srcAlpha.glId, dstAlpha.glId);
    }

    
    public static void disableBlend() {
        GlStateManager._disableBlend();
    }

    
    public static void bindMainFramebuffer() {
        bindFramebuffer(MinecraftClient.getInstance().getFramebuffer());
    }

    
    public static void bindFramebuffer(Framebuffer framebuffer) {
        if (framebuffer != null) {
            if (framebuffer.getColorAttachmentView() != null)
                bindFramebuffer(framebuffer.getColorAttachmentView(), framebuffer.getDepthAttachmentView());
        }
    }

    
    public static void bindFramebuffer(GpuTextureView colorTexture, GpuTextureView depthTexture) {
        FrameBufferUtils.bindFrameBuffer(
                FrameBufferUtils.getFrameBufferId(colorTexture, depthTexture),
                colorTexture
        );
    }

    
    public static Mesh createMesh(DrawMode drawMode, VertexFormat vertexFormat, Consumer<MeshBuilder> buildConsumer) {
        MeshBuilder meshBuilder = Mesh.builder(drawMode, vertexFormat);
        buildConsumer.accept(meshBuilder);
        return meshBuilder.buildNullable();
    }

    
    public static void draw(BuiltBuffer builtBuffer) {
        draw(BufferRenderers.MINECRAFT_BUFFER, builtBuffer, true);
    }

    
    public static void draw(BuiltBuffer builtBuffer, boolean close) {
        draw(BufferRenderers.MINECRAFT_BUFFER, builtBuffer, close);
    }

    
    public static void draw(IMesh mesh) {
        draw(BufferRenderers.COMET_BUFFER, mesh, true);
    }

    
    public static void draw(IMesh mesh, boolean close) {
        draw(BufferRenderers.COMET_BUFFER, mesh, close);
    }

    
    public static <T> void draw(BiConsumer<T, Boolean> renderConsumer, T builtBuffer, boolean close) {
        if (scissorStack.current() != null) {
            GlStateManager._enableScissorTest();
            scissorStack.current().bind();
        } else
            GlStateManager._disableScissorTest();

        globalProgram.bind();

        renderConsumer.accept(builtBuffer, close);

        globalProgram.unbind();
    }
}
