package com.sixeyes.client.render.main.program.uniform.uniforms.buffer;

import com.sixeyes.client.render.main.ChromaRenderer;
import com.sixeyes.client.render.main.buffer.BufferTarget;
import com.sixeyes.client.render.main.buffer.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlBuffer;
import org.lwjgl.opengl.GL32;

import java.util.function.BiConsumer;

public record BufferUniformUploader<T>(BiConsumer<BufferUniform, T> uploadConsumer) {
    public static final BufferUniformUploader<GpuBufferSlice> GPU_BUFFER_SLICE = new BufferUniformUploader<>(
            (bufferUniform, gpuBufferSlice) ->
                GL32.glBindBufferRange(
                        BufferTarget.UNIFORM_BUFFER.glId,
                        bufferUniform.bufferIndex,
                        ChromaRenderer.bufferIdGetter.apply((GlBuffer) gpuBufferSlice.buffer()),
                        gpuBufferSlice.offset(),
                        gpuBufferSlice.length()
                )
    );

    public static final BufferUniformUploader<GlBuffer> GL_GPU_BUFFER = new BufferUniformUploader<>(
            (bufferUniform, glGpuBuffer) ->
                GL32.glBindBufferBase(
                        BufferTarget.UNIFORM_BUFFER.glId,
                        bufferUniform.bufferIndex,
                        ChromaRenderer.bufferIdGetter.apply(glGpuBuffer)
                )
    );

    public static final BufferUniformUploader<GpuBuffer> GPU_BUFFER = new BufferUniformUploader<>(
            (bufferUniform, gpuBuffer) -> {
                GL32.glBindBufferBase(
                        BufferTarget.UNIFORM_BUFFER.glId,
                        bufferUniform.bufferIndex,
                        gpuBuffer.id
                );
            }
    );
}
