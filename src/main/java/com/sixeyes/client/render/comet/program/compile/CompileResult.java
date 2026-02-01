package com.sixeyes.client.render.comet.program.compile;

public record CompileResult(CompileStatus status, String message) {

    
    public boolean isFailure() {
        return status.equals(CompileStatus.FAILURE);
    }


}


