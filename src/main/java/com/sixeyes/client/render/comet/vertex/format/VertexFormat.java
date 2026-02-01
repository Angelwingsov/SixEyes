package com.sixeyes.client.render.comet.vertex.format;

import com.sixeyes.client.render.comet.builders.VertexFormatBuilder;
import com.sixeyes.client.render.comet.exceptions.ExceptionPrinter;
import com.sixeyes.client.render.comet.exceptions.impl.vertex.NoSuchVertexElementException;
import com.sixeyes.client.render.comet.vertex.element.VertexElement;
import com.sixeyes.client.render.comet.vertex.element.VertexElementType;
import com.sixeyes.client.render.comet.vertex.mesh.MeshBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class VertexFormat {
    
    private final HashMap<String, VertexElement> vertexMap = new HashMap<>();
    
    private final HashMap<VertexElement, String> namesMap = new HashMap<>();
    
    private final List<VertexElement> vertexElements;
    
    private final int elementsMask;
    
    private final int vertexSize;
    
    private final int[] elementOffsets;
    
    private VertexFormatBuffer vertexFormatBuffer = null;

    
    public VertexFormat(List<VertexElement> vertexElements, List<String> elementNames) {
        this.vertexElements = vertexElements;
        this.elementOffsets = new int[this.vertexElements.size()];
        this.elementsMask = vertexElements.stream().mapToInt(VertexElement::mask).reduce(0, (a, b) -> a | b);

        int size = 0;
        int elementOffset = 0;

        for (int i = 0; i < vertexElements.size(); i++) {
            VertexElement vertexElement = vertexElements.get(i);

            size += vertexElement.getSize();

            if (i > 0) {

                this.elementOffsets[i] = elementOffset;
            } else
                this.elementOffsets[i] = 0;

            elementOffset += vertexElement.getSize();


            this.vertexMap.put(elementNames.get(i), vertexElement);
            this.namesMap.put(vertexElement, elementNames.get(i));
        }

        this.vertexSize = size;
    }

    
    public int getElementsMask() {
        return this.elementsMask;
    }

    
    public Stream<VertexElement> getElementsFromMask(int mask) {
        return this.vertexElements.stream().filter(element -> element != null && (mask & element.mask()) != 0);
    }

    
    public List<VertexElement> getVertexElements() {
        return this.vertexElements;
    }

    
    public VertexElement getVertexElement(String name) {
        VertexElement vertexElement = this.vertexMap.get(name);
        if (vertexElement == null)
            ExceptionPrinter.printAndExit(new NoSuchVertexElementException(name));
        return vertexElement;
    }

    
    public String getVertexElementName(VertexElement vertexElement) {
        return this.namesMap.get(vertexElement);
    }

    
    public int getVertexSize() {
        return this.vertexSize;
    }

    
    public int[] getElementOffsets() {
        return this.elementOffsets;
    }

    
    public int getElementOffset(VertexElement vertexElement) {
        return this.elementOffsets[vertexElement.getId()];
    }

    
    public VertexFormatBuffer getVertexFormatBufferOrCreate(Supplier<VertexFormatBuffer> bufferSupplier) {
        if (this.vertexFormatBuffer == null)
            this.vertexFormatBuffer = bufferSupplier.get();

        return this.vertexFormatBuffer;
    }

    
    public static VertexFormatBuilder builder() {
        return new VertexFormatBuilder().element("Position", VertexElementType.FLOAT, 3);
    }
}


