package com.sixeyes.client.render.main.program.uniform.uniforms;

import com.sixeyes.client.render.main.program.GlProgram;
import org.lwjgl.opengl.GL20;

public class FloatUniform extends OneTypeGlUniform<Float> {
    private float primitiveVal = Float.NaN;

    public FloatUniform(String name, int location, GlProgram glProgram) {
        super(name, location, glProgram);
    }

    public void set(float val) {
        if (Float.floatToIntBits(this.primitiveVal) == Float.floatToIntBits(val)) return;
        this.primitiveVal = val;
        this.value = val;
        this.program.addUpdatedUniform(this);
    }

    @Override
    public void set(Float value) {
        if (value == null) return;
        set(value.floatValue());
    }

    @Override
    public void upload() {
        GL20.glUniform1f(getLocation(), this.primitiveVal);
    }
}
