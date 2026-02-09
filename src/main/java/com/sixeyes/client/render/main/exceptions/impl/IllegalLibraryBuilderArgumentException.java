package com.sixeyes.client.render.main.exceptions.impl;

import com.sixeyes.client.render.main.exceptions.ChromaException;

public class IllegalLibraryBuilderArgumentException extends ChromaException {
    @java.io.Serial
    private static final long serialVersionUID = 577228207931063158L;

    public IllegalLibraryBuilderArgumentException(String message) {
        super(
                "Illegal argument in library builder.",
                message,
                new String[]{
                        "You did not specify all the required arguments before building library builder",
                        "The argument you specified in library builder is null"
                },
                new String[]{
                        "Check if you are specifying all arguments when building library builder.",
                        "Check if you are not passing any arguments that are null."
                }
        );
    }
}
