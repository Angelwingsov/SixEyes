package com.sixeyes.client.render.main.program.uniform;

import com.sixeyes.client.render.main.program.GlProgram;
import lombok.Getter;

@Getter
public abstract class GlUniform {
    protected final String name;
    protected final int location;
    protected final GlProgram program;

    public GlUniform(String name, int location, GlProgram program) {
        this.name = name;
        this.location = location;
        this.program = program;
    }

    public abstract void upload();
}
