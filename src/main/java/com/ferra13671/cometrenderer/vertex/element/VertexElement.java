package com.ferra13671.cometrenderer.vertex.element;

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

    
    public int getId() {
        return id;
    }

    
    public int getCount() {
        return count;
    }

    
    public int getSize() {
        return size;
    }

    
    public VertexElementType<?> getType() {
        return type;
    }
}
