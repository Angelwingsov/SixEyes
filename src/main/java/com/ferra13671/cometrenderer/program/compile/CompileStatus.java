package com.ferra13671.cometrenderer.program.compile;

import org.lwjgl.opengl.GL11;

public enum CompileStatus {
    SUCCESSFUL(GL11.GL_TRUE),
    FAILURE(GL11.GL_FALSE);


    public final int id;


    CompileStatus(int id) {
        this.id = id;
    }


    public static CompileStatus fromStatusId(int id) {
        for (CompileStatus compileStatus : values())
            if (compileStatus.id == id)
                return compileStatus;

        return null;
    }
}
