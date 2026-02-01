package com.sixeyes.client.render.comet;

import com.sixeyes.client.render.comet.vertex.element.VertexElementType;
import com.sixeyes.client.render.comet.vertex.format.VertexFormat;

public final class CometVertexFormats {

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


