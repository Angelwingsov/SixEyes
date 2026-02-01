package com.sixeyes.client.render.comet.program.uniform.uniforms;

import com.sixeyes.client.render.comet.program.GlProgram;
import com.sixeyes.client.render.comet.program.uniform.GlUniform;
import com.sixeyes.client.render.comet.program.uniform.UniformType;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;

public class Vec4GlUniform extends OneTypeGlUniform<Vector4f> {


    public Vec4GlUniform(String name, int location, GlProgram glProgram) {
        super(name, location, glProgram);
    }

    @Override
    public void upload() {
        GL20.glUniform4f(getLocation(), this.value.x, this.value.y, this.value.z, this.value.w);
    }
}


