package com.sixeyes.client.render.main.vertex.element;

import lombok.Getter;

@Getter
public class VertexElement {
    private final int id;
    private final int count;
    private final int size;
    private final VertexElementType<?> type;

    public VertexElement(int id, int count, VertexElementType<?> type) {
        this.id = id;
        this.count = count;
        this.size = this.count * type.byteSize();
        this.type = type;
    }

    
    public int mask() {
        return 1 << this.id;
    }
}
