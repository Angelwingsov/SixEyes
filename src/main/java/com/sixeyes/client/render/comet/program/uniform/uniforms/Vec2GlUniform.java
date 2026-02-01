package com.sixeyes.client.render.comet.program.uniform.uniforms;

import com.sixeyes.client.render.comet.program.GlProgram;
import com.sixeyes.client.render.comet.program.uniform.GlUniform;
import com.sixeyes.client.render.comet.program.uniform.UniformType;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL20;

public class Vec2GlUniform extends OneTypeGlUniform<Vector2f> {

    
    public Vec2GlUniform(String name, int location, GlProgram glProgram) {
        super(name, location, glProgram);
    }

    @Override
    public void upload() {
        GL20.glUniform2f(getLocation(), this.value.x, this.value.y);
    }
}


