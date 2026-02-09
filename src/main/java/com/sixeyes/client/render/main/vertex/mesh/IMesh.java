package com.sixeyes.client.render.main.vertex.mesh;

import com.sixeyes.client.render.main.buffer.GpuBuffer;
import com.sixeyes.client.render.main.vertex.DrawMode;
import com.sixeyes.client.render.main.vertex.format.VertexFormat;

public interface IMesh extends AutoCloseable {
    int getVertexCount();
    int getIndexCount();
    
    GpuBuffer getVertexBuffer();
    GpuBuffer getIndexBuffer();
    DrawMode getDrawMode();
    VertexFormat getVertexFormat();

    @Override void close();
}
