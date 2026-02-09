package com.sixeyes.client.render.main.program.uniform.uniforms;

import com.sixeyes.client.render.main.program.GlProgram;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL20;

public class IntVec2GlUniform extends OneTypeGlUniform<Vector2i> {
    public IntVec2GlUniform(String name, int location, GlProgram glProgram) {
        super(name, location, glProgram);
    }

    @Override
    public void upload() {
        GL20.glUniform2i(getLocation(), this.value.x, this.value.y);
    }
}
