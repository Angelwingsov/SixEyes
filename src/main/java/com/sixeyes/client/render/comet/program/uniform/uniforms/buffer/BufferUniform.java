package com.sixeyes.client.render.comet.program.uniform.uniforms.buffer;

import com.sixeyes.client.render.comet.buffer.GpuBuffer;
import com.sixeyes.client.render.comet.exceptions.ExceptionPrinter;
import com.sixeyes.client.render.comet.exceptions.impl.NoSuchUniformException;
import com.sixeyes.client.render.comet.program.GlProgram;
import com.sixeyes.client.render.comet.program.uniform.GlUniform;
import com.sixeyes.client.render.comet.program.uniform.UniformType;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.gl.GlGpuBuffer;
import org.lwjgl.opengl.GL31;

public class BufferUniform extends GlUniform {
    
    private final int bufferIndex;
    
    private Runnable uploadRunnable = null;

    
    public BufferUniform(String name, int location, GlProgram glProgram) {
        super(name, location, glProgram);

        int index = GL31.glGetUniformBlockIndex(glProgram.getId(), name);
        if (index == -1) {
            ExceptionPrinter.printAndExit(new NoSuchUniformException(name, glProgram.getName()));
            bufferIndex = -1;
        } else {
            bufferIndex = glProgram.getBuffersIndexAmount() + 1;
            glProgram.setBuffersIndexAmount(bufferIndex);
            GL31.glUniformBlockBinding(glProgram.getId(), index, bufferIndex);
        }
    }

    
    public int getBufferIndex() {
        return bufferIndex;
    }

    
    public void set(GpuBufferSlice gpuBufferSlice) {
        this.uploadRunnable = () -> BufferUniformUploader.GPU_BUFFER_SLICE.uploadConsumer().accept(this, gpuBufferSlice);
        this.program.addUpdatedUniform(this);
    }

    
    public void set(GlGpuBuffer glGpuBuffer) {
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


