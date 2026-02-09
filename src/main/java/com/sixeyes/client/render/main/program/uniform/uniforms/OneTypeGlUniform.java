package com.sixeyes.client.render.main.program.uniform.uniforms;

import com.sixeyes.client.render.main.program.GlProgram;
import com.sixeyes.client.render.main.program.uniform.GlUniform;

public abstract class OneTypeGlUniform<T> extends GlUniform {
    protected T value = null;

    public OneTypeGlUniform(String name, int location, GlProgram glProgram) {
        super(name, location, glProgram);
    }

    public void set(T value) {
        this.value = value;
        this.program.addUpdatedUniform(this);
    }
}
