package com.sixeyes.client.render.comet.program.uniform;

import com.sixeyes.client.render.comet.program.GlProgram;
import com.sixeyes.client.render.comet.program.uniform.uniforms.sampler.SamplerUniform;

public abstract class GlUniform {
    
    protected final String name;
    
    protected final int location;
    
    protected final GlProgram program;

    
    public GlUniform(String name, int location, GlProgram program) {
        this.name = name;
        this.location = location;
        this.program = program;
    }

    
    public String getName() {
        return name;
    }

    
    public int getLocation() {
        return location;
    }

    
    public GlProgram getProgram() {
        return program;
    }

    
    public abstract void upload();
}


