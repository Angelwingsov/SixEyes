package com.sixeyes.client.render.comet.vertex.mesh;

import com.sixeyes.client.render.comet.buffer.GpuBuffer;
import com.sixeyes.client.render.comet.vertex.DrawMode;
import com.sixeyes.client.render.comet.vertex.format.VertexFormat;

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


