package com.sixeyes.client.render.main.buffer;

import com.sixeyes.client.render.main.Bindable;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;

public class GpuBuffer implements Bindable, AutoCloseable {
    public final int id;
    public final BufferUsage usage;
    public final BufferTarget target;
    private boolean closed = false;
    
    public GpuBuffer(ByteBuffer data, BufferUsage usage, BufferTarget target) {
        this.id = GL15.glGenBuffers();
        this.usage = usage;
        this.target = target;
        bind();
        GL15.glBufferData(target.glId, data, usage.glId);
        GL15.glBindBuffer(target.glId, 0);
    }

    @Override
    public void bind() {
        GL15.glBindBuffer(this.target.glId, this.id);
    }

    @Override
    public void unbind() {
        GL15.glBindBuffer(this.target.glId, 0);
    }

    @Override
    public void close() {
        if (!this.closed)
            GL15.glDeleteBuffers(this.id);
        this.closed = true;
    }
}
