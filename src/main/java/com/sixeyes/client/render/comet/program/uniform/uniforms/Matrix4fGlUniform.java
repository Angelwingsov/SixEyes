package com.sixeyes.client.render.comet.program.uniform.uniforms;

import com.sixeyes.client.render.comet.program.GlProgram;
import com.sixeyes.client.render.comet.program.uniform.GlUniform;
import com.sixeyes.client.render.comet.program.uniform.UniformType;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class Matrix4fGlUniform extends GlUniform {
    
    private final FloatBuffer buffer = MemoryUtil.memAllocFloat(16);

    
    public Matrix4fGlUniform(String name, int location, GlProgram glProgram) {
        super(name, location, glProgram);
    }

    
    public void set(Matrix4f matrix4f) {
        matrix4f.get(this.buffer);
        this.program.addUpdatedUniform(this);
    }

    @Override
    public void upload() {
        GL20.glUniformMatrix4fv(getLocation(), false, this.buffer);
    }
}


