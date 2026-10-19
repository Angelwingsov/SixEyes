package com.sixeyes.client.render.main.buffer;

import java.util.ArrayDeque;
import java.util.Deque;

public final class GpuBufferPool {
    private static final int MAX_POOL_SIZE = 16;
    private static final int INITIAL_VBO_CAPACITY = 65536;

    private static final Deque<GpuBuffer> VERTEX_BUFFER_POOL = new ArrayDeque<>();

    public static volatile int poolHits = 0;
    public static volatile int poolMisses = 0;

    private GpuBufferPool() {}

    public static synchronized GpuBuffer acquireVertexBuffer() {
        GpuBuffer buffer = VERTEX_BUFFER_POOL.pollFirst();
        if (buffer != null) {
            poolHits++;
            return buffer;
        }

        poolMisses++;
        return new GpuBuffer(BufferTarget.ARRAY_BUFFER, BufferUsage.STREAM_DRAW, INITIAL_VBO_CAPACITY);
    }

    public static synchronized void releaseVertexBuffer(GpuBuffer buffer) {
        if (buffer != null && VERTEX_BUFFER_POOL.size() < MAX_POOL_SIZE) {
            VERTEX_BUFFER_POOL.addLast(buffer);
        } else if (buffer != null) {
            buffer.close();
        }
    }

    public static synchronized void cleanup() {
        while (!VERTEX_BUFFER_POOL.isEmpty()) {
            GpuBuffer buffer = VERTEX_BUFFER_POOL.pollFirst();
            if (buffer != null) {
                buffer.close();
            }
        }
        poolHits = 0;
        poolMisses = 0;
    }
}
