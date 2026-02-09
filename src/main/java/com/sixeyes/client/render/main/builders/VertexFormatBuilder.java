package com.sixeyes.client.render.main.builders;

import com.sixeyes.client.render.main.vertex.element.VertexElement;
import com.sixeyes.client.render.main.vertex.element.VertexElementType;
import com.sixeyes.client.render.main.vertex.format.VertexFormat;

import java.util.ArrayList;
import java.util.List;

public final class VertexFormatBuilder {
    private final List<VertexElement> vertexElements = new ArrayList<>();
    private final List<String> elementNames = new ArrayList<>();
    public VertexFormatBuilder() {}

    public VertexFormatBuilder element(String name, VertexElementType<?> type, int count) {
        int id = vertexElements.size();
        elementNames.add(name);
        vertexElements.add(new VertexElement(id, count, type));

        return this;
    }
    
    public VertexFormat build() {
        return new VertexFormat(vertexElements, elementNames);
    }
}
