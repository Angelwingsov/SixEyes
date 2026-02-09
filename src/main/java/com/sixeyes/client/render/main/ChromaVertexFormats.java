package com.sixeyes.client.render.main;

import com.sixeyes.client.render.main.vertex.element.VertexElementType;
import com.sixeyes.client.render.main.vertex.format.VertexFormat;

public final class ChromaVertexFormats {
    public static final VertexFormat POSITION = VertexFormat.builder().build();

    public static final VertexFormat POSITION_COLOR = VertexFormat.builder()
            .element("Color", VertexElementType.FLOAT, 4)
            .build();

    public static final VertexFormat POSITION_TEXTURE = VertexFormat.builder()
            .element("Texture", VertexElementType.FLOAT, 2)
            .build();

    public static final VertexFormat POSITION_COLOR_TEXTURE = VertexFormat.builder()
            .element("Color", VertexElementType.FLOAT, 4)
            .element("Texture", VertexElementType.FLOAT, 2)
            .build();

    public static final VertexFormat POSITION_TEXTURE_COLOR = VertexFormat.builder()
            .element("Texture", VertexElementType.FLOAT, 2)
            .element("Color", VertexElementType.FLOAT, 4)
            .build();
}
