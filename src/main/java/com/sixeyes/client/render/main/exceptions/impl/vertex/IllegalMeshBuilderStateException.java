package com.sixeyes.client.render.main.exceptions.impl.vertex;

import com.sixeyes.client.render.main.exceptions.ChromaException;

public class IllegalMeshBuilderStateException extends ChromaException {

    @java.io.Serial
    private static final long serialVersionUID = 6447527243741543095L;

    public IllegalMeshBuilderStateException(String details, String[] reasons, String[] solutions) {
        super(
                "Illegal MeshBuilder state.",
                details,
                reasons,
                solutions
        );
    }
}
