package com.ferra13671.cometrenderer.program.compile;

public record CompileResult(CompileStatus status, String message) {

    
    public boolean isFailure() {
        return status.equals(CompileStatus.FAILURE);
    }


}
