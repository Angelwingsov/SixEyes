package com.sixeyes.client.render.comet.program.uniform.uniforms;

import com.sixeyes.client.render.comet.program.GlProgram;
import com.sixeyes.client.render.comet.program.uniform.GlUniform;
import com.sixeyes.client.render.comet.program.uniform.UniformType;

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


