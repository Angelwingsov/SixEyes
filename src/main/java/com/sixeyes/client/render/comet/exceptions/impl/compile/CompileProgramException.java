package com.sixeyes.client.render.comet.exceptions.impl.compile;

import com.sixeyes.client.render.comet.exceptions.CometException;

public class CompileProgramException extends CometException {
    @java.io.Serial
    private static final long serialVersionUID = -8371118021144864550L;

    public CompileProgramException(String programName, String reason) {
        super(
                "Compile program error.",
                String.format("Error compiling program '%s', reason:\n%s", programName, reason),
                new String[]{
                        "Comet Renderer malfunction",
                        "OpenGL malfunction (Very Rare)",
                        "Out of GPU Memory (Very Rare)"
                },
                new String[]{
                        "Contact Ferra13671",
                        "Check the integrity of the OpenGL library",
                        "Make sure you are not overloading the GPU memory (e.g. by loading something into the GPU in a infinity loop)"
                }
        );
    }
}


