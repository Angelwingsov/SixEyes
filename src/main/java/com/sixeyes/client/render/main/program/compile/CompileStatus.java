package com.sixeyes.client.render.main.program.compile;

import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL11;

@RequiredArgsConstructor
public enum CompileStatus {
    SUCCESSFUL(GL11.GL_TRUE),
    FAILURE(GL11.GL_FALSE);

    public final int id;

    public static CompileStatus fromStatusId(int id) {
        for (CompileStatus compileStatus : values())
            if (compileStatus.id == id)
                return compileStatus;

        return null;
    }
}
