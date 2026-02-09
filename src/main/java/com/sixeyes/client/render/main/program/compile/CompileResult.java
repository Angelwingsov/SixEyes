package com.sixeyes.client.render.main.program.compile;

public record CompileResult(CompileStatus status, String message) {
    public boolean isFailure() {
        return status.equals(CompileStatus.FAILURE);
    }
}
