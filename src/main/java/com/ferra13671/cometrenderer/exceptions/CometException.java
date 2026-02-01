package com.ferra13671.cometrenderer.exceptions;

public class CometException extends RuntimeException {

    protected final String description;

    protected final String details;

    protected final String[] reasons;

    protected final String[] solutions;


    public CometException(String description, String details, String[] reasons, String[] solutions) {
        this.description = description;
        this.details = details;
        this.reasons = reasons;
        this.solutions = solutions;
    }


    public String getDescription() {
        return description;
    }


    public String getDetails() {
        return details;
    }


    public String[] getReasons() {
        return reasons;
    }


    public String[] getSolutions() {
        return solutions;
    }
}
