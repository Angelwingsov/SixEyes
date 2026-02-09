package com.sixeyes.client.render.main.vertex.format.uploader;

import com.sixeyes.client.render.main.buffer.GpuBuffer;
import com.sixeyes.client.render.main.vertex.format.VertexFormat;
import com.sixeyes.client.render.main.vertex.format.VertexFormatBuffer;

public abstract class VertexFormatBufferUploader {
    public abstract void applyFormatToBuffer(GpuBuffer vertexBuffer, VertexFormat vertexFormat);
    public abstract VertexFormatBuffer createVertexFormatBuffer(VertexFormat vertexFormat);
}
