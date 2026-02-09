package com.sixeyes.client.render.main.vertex.format;

import com.sixeyes.client.render.main.buffer.GpuBuffer;

import java.util.concurrent.atomic.AtomicReference;

public record VertexFormatBuffer(int glId, VertexFormat vertexFormat, AtomicReference<GpuBuffer> buffer) {}
