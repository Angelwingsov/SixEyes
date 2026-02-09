package com.sixeyes.client.render.main;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlBuffer;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.sixeyes.client.render.main.blend.DstFactor;
import com.sixeyes.client.render.main.blend.SrcFactor;
import com.sixeyes.client.render.main.program.GlProgram;
import com.sixeyes.client.render.main.program.GlProgramSnippet;
import com.sixeyes.client.render.main.program.uniform.UniformType;
import com.sixeyes.client.render.main.program.uniform.uniforms.Matrix4fGlUniform;
import com.sixeyes.client.render.main.program.uniform.uniforms.buffer.BufferUniform;
import com.sixeyes.client.render.main.scissor.ScissorStack;
import com.sixeyes.client.render.main.vertex.DrawMode;
import com.sixeyes.client.render.main.vertex.format.VertexFormat;
import com.sixeyes.client.render.main.vertex.mesh.IMesh;
import com.sixeyes.client.render.main.vertex.mesh.Mesh;
import com.sixeyes.client.render.main.vertex.mesh.MeshBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChromaRenderer {
    private static boolean initialized = false;

    public static final Logger logger = LoggerFactory.getLogger("ChromaRenderer");
    public static Function<GlBuffer, Integer> bufferIdGetter;
    public static Supplier<Integer> scaleGetter;

    public static final GlProgramSnippet matrixSnippet = ChromaLoaders.IN_JAR.createProgramBuilder()
            .uniform("ProjMat", UniformType.BUFFER)
            .uniform("ModelViewMat", UniformType.MATRIX)
            .buildSnippet();

    public static GlProgram globalProgram;
    public static final ScissorStack scissorStack = new ScissorStack();

    private static final ConcurrentLinkedQueue<ByteBufferBuilder> BUFFER_POOL = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<MeshBuilder> MESH_BUILDER_POOL = new ConcurrentLinkedQueue<>();
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;
    private static final int SMALL_BUFFER_SIZE = 256 * 1024;

    public static volatile int bufferPoolHits = 0;
    public static volatile int bufferPoolMisses = 0;

    public static void init(Function<GlBuffer, Integer> bufferIdGetter, Supplier<Integer> scaleGetter) {
        if (initialized)
            throw new IllegalStateException("Renderer has already initialized.");

        ChromaRenderer.bufferIdGetter = bufferIdGetter;
        ChromaRenderer.scaleGetter = scaleGetter;
        initialized = true;
    }

    public static void initMatrix() {
        BufferUniform projectionUniform = globalProgram.getUniform("ProjMat", UniformType.BUFFER);
        if (projectionUniform != null) {
            GpuBufferSlice slice = RenderSystem.getProjectionMatrixBuffer();
            projectionUniform.set(slice);
        }

        Matrix4fGlUniform modelViewUniform = globalProgram.getUniform("ModelViewMat", UniformType.MATRIX);
        if (modelViewUniform != null)
            modelViewUniform.set(RenderSystem.getModelViewMatrix());
    }

    public static void setGlobalProgram(GlProgram globalProgram) {
        ChromaRenderer.globalProgram = globalProgram;
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
        bindFramebuffer(Minecraft.getInstance().getMainRenderTarget());
    }

    public static void bindFramebuffer(RenderTarget framebuffer) {
        if (framebuffer != null) {
            if (framebuffer.getColorTextureView() != null)
                bindFramebuffer(framebuffer.getColorTextureView(), framebuffer.getDepthTextureView());
        }
    }

    public static void bindFramebuffer(GpuTextureView colorTexture, GpuTextureView depthTexture) {
        FrameBufferUtils.bindFrameBuffer(
                FrameBufferUtils.getFrameBufferId(colorTexture, depthTexture),
                colorTexture
        );
    }

    private static ByteBufferBuilder getPooledBuffer(int size) {
        ByteBufferBuilder buffer = BUFFER_POOL.poll();
        if (buffer == null) {
            buffer = new ByteBufferBuilder(size);
            bufferPoolMisses++;
        } else {
            try {
                buffer.clear();
                bufferPoolHits++;
            } catch (IllegalStateException e) {
                buffer = new ByteBufferBuilder(size);
                bufferPoolMisses++;
            }
        }
        return buffer;
    }
    
    private static void returnBuffer(ByteBufferBuilder buffer) {
        if (buffer != null && BUFFER_POOL.size() < 16) {
            try {
                buffer.discard();
                BUFFER_POOL.offer(buffer);
            } catch (IllegalStateException e) {
            }
        }
    }
    
    private static MeshBuilder getPooledMeshBuilder(DrawMode drawMode, VertexFormat vertexFormat) {
        MeshBuilder meshBuilder = MESH_BUILDER_POOL.poll();
        if (meshBuilder == null) {
            ByteBufferBuilder buffer = getPooledBuffer(DEFAULT_BUFFER_SIZE);
            meshBuilder = new MeshBuilder(buffer, drawMode, vertexFormat, false);
        } else {
            ByteBufferBuilder buffer = getPooledBuffer(DEFAULT_BUFFER_SIZE);
            meshBuilder.set(buffer, drawMode, vertexFormat, false);
        }
        return meshBuilder;
    }
    
    private static void returnMeshBuilder(MeshBuilder meshBuilder) {
        if (meshBuilder != null && MESH_BUILDER_POOL.size() < 8) {
            MESH_BUILDER_POOL.offer(meshBuilder);
        }
    }

    public static Mesh createMesh(DrawMode drawMode, VertexFormat vertexFormat, Consumer<MeshBuilder> buildConsumer) {
        MeshBuilder meshBuilder = getPooledMeshBuilder(drawMode, vertexFormat);
        ByteBufferBuilder buffer = meshBuilder.bufferAllocator;
        try {
            buildConsumer.accept(meshBuilder);
            Mesh result = meshBuilder.buildNullable();
            if (result != null) {
                returnMeshBuilder(meshBuilder);
                return result;
            }
        } catch (Exception e) {
            returnBuffer(buffer);
            returnMeshBuilder(meshBuilder);
            throw e;
        }
        returnBuffer(buffer);
        returnMeshBuilder(meshBuilder);
        return null;
    }
    
    public static Mesh createSmallMesh(DrawMode drawMode, VertexFormat vertexFormat, Consumer<MeshBuilder> buildConsumer) {
        ByteBufferBuilder buffer = getPooledBuffer(SMALL_BUFFER_SIZE);
        MeshBuilder meshBuilder = new MeshBuilder(buffer, drawMode, vertexFormat, false);
        try {
            buildConsumer.accept(meshBuilder);
            Mesh result = meshBuilder.buildNullable();
            if (result == null) {
                returnBuffer(buffer);
            }
            return result;
        } catch (Exception e) {
            returnBuffer(buffer);
            throw e;
        }
    }

    public static void draw(MeshData builtBuffer) {
        draw(BufferRenderers.MINECRAFT_BUFFER, builtBuffer, true);
    }

    public static void draw(MeshData builtBuffer, boolean close) {
        draw(BufferRenderers.MINECRAFT_BUFFER, builtBuffer, close);
    }

    public static void draw(IMesh mesh) {
        draw(BufferRenderers.CUSTOM_BUFFER, mesh, true);
    }

    public static void draw(IMesh mesh, boolean close) {
        draw(BufferRenderers.CUSTOM_BUFFER, mesh, close);
    }

    public static <T> void draw(BiConsumer<T, Boolean> renderConsumer, T builtBuffer, boolean close) {
        globalProgram.bind();
        renderConsumer.accept(builtBuffer, close);
        globalProgram.unbind();
    }

    public static void cleanup() {
        BUFFER_POOL.clear();
        MESH_BUILDER_POOL.clear();
        bufferPoolHits = 0;
        bufferPoolMisses = 0;
    }
}