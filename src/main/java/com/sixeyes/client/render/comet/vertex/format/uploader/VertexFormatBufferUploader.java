package com.sixeyes.client.render.comet.vertex.format.uploader;

import com.sixeyes.client.render.comet.buffer.GpuBuffer;
import com.sixeyes.client.render.comet.vertex.format.VertexFormat;
import com.sixeyes.client.render.comet.vertex.format.VertexFormatBuffer;

public abstract class VertexFormatBufferUploader {

    public abstract void applyFormatToBuffer(GpuBuffer vertexBuffer, VertexFormat vertexFormat);

    public abstract VertexFormatBuffer createVertexFormatBuffer(VertexFormat vertexFormat);
}


