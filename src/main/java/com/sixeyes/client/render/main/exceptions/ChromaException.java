package com.sixeyes.client.render.main.exceptions;

import lombok.Getter;

@Getter
public class ChromaException extends RuntimeException {
    protected final String description;
    protected final String details;
    protected final String[] reasons;
    protected final String[] solutions;

    public ChromaException(String description, String details, String[] reasons, String[] solutions) {
        this.description = description;
        this.details = details;
        this.reasons = reasons;
        this.solutions = solutions;
    }
}
