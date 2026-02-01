package com.ferra13671.cometrenderer.program.uniform.uniforms;

import com.ferra13671.cometrenderer.program.GlProgram;
import com.ferra13671.cometrenderer.program.uniform.GlUniform;
import com.ferra13671.cometrenderer.program.uniform.UniformType;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL20;

public class IntVec3GlUniform extends OneTypeGlUniform<Vector3i> {


    public IntVec3GlUniform(String name, int location, GlProgram glProgram) {
        super(name, location, glProgram);
    }

    @Override
    public void upload() {
        GL20.glUniform3i(getLocation(), this.value.x, this.value.y, this.value.z);
    }
}
