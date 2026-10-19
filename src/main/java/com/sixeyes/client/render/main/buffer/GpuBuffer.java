package com.sixeyes.client.render.main.buffer;

import com.sixeyes.client.render.main.Bindable;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;

public class GpuBuffer implements Bindable, AutoCloseable {
    public final int id;
    public BufferUsage usage;
    public final BufferTarget target;
    private int capacity = 0;
    private boolean closed = false;
    
    public GpuBuffer(ByteBuffer data, BufferUsage usage, BufferTarget target) {
        this.id = GL15.glGenBuffers();
        this.usage = usage;
        this.target = target;
        this.capacity = data.remaining();
        bind();
        GL15.glBufferData(target.glId, data, usage.glId);
        GL15.glBindBuffer(target.glId, 0);
    }

    public GpuBuffer(BufferTarget target, BufferUsage usage, int initialCapacity) {
        this.id = GL15.glGenBuffers();
        this.usage = usage;
        this.target = target;
        this.capacity = Math.max(initialCapacity, 4096);
        bind();
        GL15.glBufferData(target.glId, this.capacity, usage.glId);
        GL15.glBindBuffer(target.glId, 0);
    }

    public void upload(ByteBuffer data, BufferUsage usage) {
        this.usage = usage;
        bind();
        int bytes = data.remaining();
        if (bytes > this.capacity) {
            this.capacity = Math.max(bytes, Math.max(this.capacity * 2, 65536));
            GL15.glBufferData(this.target.glId, this.capacity, usage.glId);
        } else {
        }
        GL15.glBufferSubData(this.target.glId, 0, data);
        GL15.glBindBuffer(this.target.glId, 0);
    }

    public int getCapacity() {
        return capacity;
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

