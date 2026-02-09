package com.sixeyes.client.render.main.exceptions.impl;

import com.sixeyes.client.render.main.exceptions.ChromaException;

public class NoSuchUniformException extends ChromaException {
    @java.io.Serial
    private static final long serialVersionUID = 1079198928491527937L;

    public NoSuchUniformException(String uniformName, String programName) {
        super(
                "No such uniform error.",
                String.format("Cannot find uniform '%s' in program '%s'.", uniformName, programName),
                new String[]{
                        "The uniform added to the program schema is not present in program itself",
                        "You are trying to find a uniform that is not in the program schema"
                },
                new String[]{
                        "Make sure the uniform is in both schema and program",
                        "Make sure you are search the right uniform and have not made a mistake in its name"
                }
        );
    }
}
