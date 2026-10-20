package com.sixeyes.client.render.main.program.uniform.uniforms.buffer;

import com.sixeyes.client.render.main.buffer.GpuBuffer;
import com.sixeyes.client.render.main.exceptions.ExceptionPrinter;
import com.sixeyes.client.render.main.exceptions.impl.NoSuchUniformException;
import com.sixeyes.client.render.main.program.GlProgram;
import com.sixeyes.client.render.main.program.uniform.GlUniform;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.backend.opengl.GlBuffer;
import org.lwjgl.opengl.GL31;

public class BufferUniform extends GlUniform {
    public final int bufferIndex;
    
    private GpuBufferSlice currentSlice;
    private GlBuffer currentGlBuffer;
    private GpuBuffer currentGpuBuffer;
    private Runnable customRunnable;
    private int uploadMode = 0;

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
        this.currentSlice = gpuBufferSlice;
        this.uploadMode = 1;
        this.program.addUpdatedUniform(this);
    }

    public void set(GlBuffer glGpuBuffer) {
        this.currentGlBuffer = glGpuBuffer;
        this.uploadMode = 2;
        this.program.addUpdatedUniform(this);
    }

    public void set(GpuBuffer gpuBuffer) {
        this.currentGpuBuffer = gpuBuffer;
        this.uploadMode = 3;
        this.program.addUpdatedUniform(this);
    }

    public <T> void set(BufferUniformUploader<T> uploader, T buffer) {
        this.customRunnable = () -> uploader.uploadConsumer().accept(this, buffer);
        this.uploadMode = 4;
        this.program.addUpdatedUniform(this);
    }

    @Override
    public void upload() {
        switch (this.uploadMode) {
            case 1 -> {
                if (this.currentSlice != null) {
                    BufferUniformUploader.GPU_BUFFER_SLICE.uploadConsumer().accept(this, this.currentSlice);
                }
            }
            case 2 -> {
                if (this.currentGlBuffer != null) {
                    BufferUniformUploader.GL_GPU_BUFFER.uploadConsumer().accept(this, this.currentGlBuffer);
                }
            }
            case 3 -> {
                if (this.currentGpuBuffer != null) {
                    BufferUniformUploader.GPU_BUFFER.uploadConsumer().accept(this, this.currentGpuBuffer);
                }
            }
            case 4 -> {
                if (this.customRunnable != null) {
                    this.customRunnable.run();
                }
            }
        }
    }
}
