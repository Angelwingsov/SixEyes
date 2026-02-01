package com.sixeyes.client.render.comet.vertex.mesh;

import com.sixeyes.client.render.comet.buffer.BufferTarget;
import com.sixeyes.client.render.comet.buffer.BufferUsage;
import com.sixeyes.client.render.comet.buffer.GpuBuffer;
import com.sixeyes.client.render.comet.vertex.DrawMode;
import com.sixeyes.client.render.comet.vertex.IndexBufferGenerator;
import com.sixeyes.client.render.comet.vertex.format.VertexFormat;
import net.minecraft.client.util.BufferAllocator;

import java.nio.ByteBuffer;

public class Mesh implements IMesh {
    
    private final VertexFormat vertexFormat;
    
    private final int vertexCount;
    
    private final int indexCount;
    
    private DrawMode drawMode;
    
    private final GpuBuffer vertexBuffer;
    
    private GpuBuffer indexBuffer;
    
    private boolean standalone = false;

    
    public Mesh(ByteBuffer byteBuffer, VertexFormat vertexFormat, int vertexCount, int indexCount, DrawMode drawMode, Runnable afterInitRunnable) {
        this.vertexFormat = vertexFormat;
        this.vertexCount = vertexCount;
        this.indexCount = indexCount;
        this.drawMode = drawMode;

        this.vertexBuffer = new GpuBuffer(byteBuffer, BufferUsage.STATIC_DRAW, BufferTarget.ARRAY_BUFFER);

        afterInitRunnable.run();
    }

    
    public Mesh makeStandalone() {
        if (!this.standalone) {
            this.standalone = true;
            recreateIndexBuffer();
        }

        return this;
    }

    private void recreateIndexBuffer() {
        if (this.indexBuffer != null)
            this.indexBuffer.close();

        IndexBufferGenerator indexBufferGenerator = this.drawMode.indexBufferGenerator();
        if (indexBufferGenerator != null)
            this.indexBuffer = indexBufferGenerator.getIndexBuffer(this.indexCount, true);
    }

    
    public void changeDrawMode(DrawMode drawMode) {
        this.drawMode = drawMode;
        if (this.standalone)
            recreateIndexBuffer();
    }

    @Override
    public int getVertexCount() {
        return this.vertexCount;
    }

    @Override
    public int getIndexCount() {
        return this.indexCount;
    }

    @Override
    public GpuBuffer getVertexBuffer() {
        return this.vertexBuffer;
    }

    @Override
    public GpuBuffer getIndexBuffer() {
        return this.standalone ? this.indexBuffer : this.drawMode.indexBufferGenerator().getIndexBuffer(this.indexCount, false);
    }

    @Override
    public DrawMode getDrawMode() {
        return this.drawMode;
    }

    @Override
    public VertexFormat getVertexFormat() {
        return this.vertexFormat;
    }

    @Override
    public void close() {
        this.vertexBuffer.close();
        if (this.indexBuffer != null)
            this.indexBuffer.close();
    }

    
    public static MeshBuilder builder(DrawMode drawMode, VertexFormat vertexFormat) {
        return builder(786432, drawMode, vertexFormat);
    }

    
    public static MeshBuilder builder(int size, DrawMode drawMode, VertexFormat vertexFormat) {
        return builder(new BufferAllocator(size), drawMode, vertexFormat, true);
    }

    
    public static MeshBuilder builder(BufferAllocator bufferAllocator, DrawMode drawMode, VertexFormat vertexFormat, boolean closeAllocatorAfterBuild) {
        return new MeshBuilder(bufferAllocator, drawMode, vertexFormat, closeAllocatorAfterBuild);
    }
}


