package com.sixeyes.client.render.main.program.uniform.uniforms;

import com.sixeyes.client.render.main.program.GlProgram;
import org.lwjgl.opengl.GL20;

public class FloatArrayUniform extends OneTypeGlUniform<float[]> {
    public FloatArrayUniform(String name, int location, GlProgram glProgram) {
        super(name, location, glProgram);
    }

    @Override
    public void upload() {
        GL20.glUniform1fv(getLocation(), this.value);
    }
}
