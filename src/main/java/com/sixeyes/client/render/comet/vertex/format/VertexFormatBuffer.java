package com.sixeyes.client.render.comet.vertex.format;

import com.sixeyes.client.render.comet.buffer.GpuBuffer;
import com.sixeyes.client.render.comet.vertex.format.uploader.VertexFormatBufferUploader;

import java.util.concurrent.atomic.AtomicReference;

public record VertexFormatBuffer(int glId, VertexFormat vertexFormat, AtomicReference<GpuBuffer> buffer) {}


