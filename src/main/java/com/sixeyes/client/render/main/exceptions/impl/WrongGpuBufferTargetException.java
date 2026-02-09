package com.sixeyes.client.render.main.exceptions.impl;

import com.sixeyes.client.render.main.exceptions.ChromaException;

public class WrongGpuBufferTargetException extends ChromaException {
    @java.io.Serial
    private static final long serialVersionUID = -1616138098256794198L;

    public WrongGpuBufferTargetException(int givenTarget, int requiredTaget) {
        super(
                "Wrong gpu buffer target.",
                String.format("The received gpu buffer has target '%s', but '%s' was expected..", givenTarget, requiredTaget),
                new String[]{
                        "You are giving the method that caused the error an wrong gpu buffer"
                },
                new String[]{
                        "Recheck the method call that caused the error and fix the buffer issue"
                }
        );
    }
}
