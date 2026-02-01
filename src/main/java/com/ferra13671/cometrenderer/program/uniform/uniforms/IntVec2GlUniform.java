package com.ferra13671.cometrenderer.program.uniform.uniforms;

import com.ferra13671.cometrenderer.program.GlProgram;
import com.ferra13671.cometrenderer.program.uniform.GlUniform;
import com.ferra13671.cometrenderer.program.uniform.UniformType;
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
