package com.ferra13671.cometrenderer.program.uniform.uniforms;

import com.ferra13671.cometrenderer.program.GlProgram;
import com.ferra13671.cometrenderer.program.uniform.GlUniform;
import com.ferra13671.cometrenderer.program.uniform.UniformType;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

public class Vec3GlUniform extends OneTypeGlUniform<Vector3f> {

    
    public Vec3GlUniform(String name, int location, GlProgram glProgram) {
        super(name, location, glProgram);
    }

    @Override
    public void upload() {
        GL20.glUniform3f(getLocation(), this.value.x, this.value.y, this.value.z);
    }
}
