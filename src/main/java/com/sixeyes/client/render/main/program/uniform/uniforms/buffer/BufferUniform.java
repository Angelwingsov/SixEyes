package com.sixeyes.client.render.main.program.uniform.uniforms.buffer;

import com.sixeyes.client.render.main.buffer.GpuBuffer;
import com.sixeyes.client.render.main.exceptions.ExceptionPrinter;
import com.sixeyes.client.render.main.exceptions.impl.NoSuchUniformException;
import com.sixeyes.client.render.main.program.GlProgram;
import com.sixeyes.client.render.main.program.uniform.GlUniform;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlBuffer;
import org.lwjgl.opengl.GL31;

public class BufferUniform extends GlUniform {
    public final int bufferIndex;
    private Runnable uploadRunnable = null;

    public BufferUniform(String name, int location, GlProgram glProgram) {
        super(name, location, glProgram);

        int index = GL31.glGetUniformBlockIndex(glProgram.id, name);
        if (index == -1) {
            ExceptionPrinter.printAndExit(new NoSuchUniformException(name, glProgram.name));
            bufferIndex = -1;
        } else {
            bufferIndex = glProgram.buffersIndexAmount + 1;
            glProgram.buffersIndexAmount = bufferIndex;
            GL31.glUniformBlockBinding(glProgram.id, index, bufferIndex);
        }
    }

    public void set(GpuBufferSlice gpuBufferSlice) {
        this.uploadRunnable = () -> BufferUniformUploader.GPU_BUFFER_SLICE.uploadConsumer().accept(this, gpuBufferSlice);
        this.program.addUpdatedUniform(this);
    }

    public void set(GlBuffer glGpuBuffer) {
        this.uploadRunnable = () -> BufferUniformUploader.GL_GPU_BUFFER.uploadConsumer().accept(this, glGpuBuffer);
        this.program.addUpdatedUniform(this);
    }

    public void set(GpuBuffer gpuBuffer) {
        this.uploadRunnable = () -> BufferUniformUploader.GPU_BUFFER.uploadConsumer().accept(this, gpuBuffer);
        this.program.addUpdatedUniform(this);
    }

    public <T> void set(BufferUniformUploader<T> uploader, T buffer) {
        this.uploadRunnable = () -> uploader.uploadConsumer().accept(this, buffer);
        this.program.addUpdatedUniform(this);
    }

    @Override
    public void upload() {
        if (this.uploadRunnable != null)
            this.uploadRunnable.run();
    }
}
