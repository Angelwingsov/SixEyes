package com.ferra13671.cometrenderer.program.uniform.uniforms;

import com.ferra13671.cometrenderer.program.GlProgram;
import com.ferra13671.cometrenderer.program.uniform.GlUniform;
import com.ferra13671.cometrenderer.program.uniform.UniformType;
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
