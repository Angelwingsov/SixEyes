package com.sixeyes.client.render.comet.program.uniform.uniforms;

import com.sixeyes.client.render.comet.program.GlProgram;
import com.sixeyes.client.render.comet.program.uniform.GlUniform;
import com.sixeyes.client.render.comet.program.uniform.UniformType;
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


