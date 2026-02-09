package com.sixeyes.client.render.main.program.uniform.uniforms;

import com.sixeyes.client.render.main.program.GlProgram;
import org.lwjgl.opengl.GL20;

public class IntUniform extends OneTypeGlUniform<Integer> {
    public IntUniform(String name, int location, GlProgram glProgram) {
        super(name, location, glProgram);
    }

    @Override
    public void upload() {
        GL20.glUniform1i(getLocation(), this.value);
    }
}
