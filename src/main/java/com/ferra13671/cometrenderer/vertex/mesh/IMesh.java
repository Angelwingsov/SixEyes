package com.ferra13671.cometrenderer.vertex.mesh;

import com.ferra13671.cometrenderer.buffer.GpuBuffer;
import com.ferra13671.cometrenderer.vertex.DrawMode;
import com.ferra13671.cometrenderer.vertex.format.VertexFormat;

public interface IMesh extends AutoCloseable {

    
    int getVertexCount();

    
    int getIndexCount();

    
    GpuBuffer getVertexBuffer();

    
    GpuBuffer getIndexBuffer();

    
    DrawMode getDrawMode();

    
    VertexFormat getVertexFormat();

    
    @Override
    void close();
}
