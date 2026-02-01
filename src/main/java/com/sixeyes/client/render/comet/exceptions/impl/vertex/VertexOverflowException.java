package com.sixeyes.client.render.comet.exceptions.impl.vertex;

import com.sixeyes.client.render.comet.exceptions.CometException;
import com.sixeyes.client.render.comet.vertex.mesh.MeshBuilder;

public class VertexOverflowException extends CometException {

    @java.io.Serial
    private static final long serialVersionUID = 2211120929779008363L;

    public VertexOverflowException() {
        super(
                "VertexBuilder overflow.",
                String.format("The number of vertices in VertexBuilder exceeded the maximum value (%s).", MeshBuilder.MAX_VERTICES),
                new String[]{
                        "You may be adding vertices in an infinite loop.",
                        "You're a monster who managed to manually exceed the maximum number of vertices."
                },
                new String[]{
                        "Check the method where you add vertices to VertexBuilder and fix it."
                }
        );
    }
}


