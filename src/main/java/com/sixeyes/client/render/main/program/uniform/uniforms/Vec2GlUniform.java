package com.sixeyes.client.render.main.program.uniform.uniforms;

import com.sixeyes.client.render.main.program.GlProgram;
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
