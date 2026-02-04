package com.sixeyes.client.api.render;

public interface Batchable {
    void begin();
    void flush(RenderPipeline pipeline);
    void end();
}


