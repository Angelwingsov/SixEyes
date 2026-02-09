package com.sixeyes.client.render.main.program;

import com.sixeyes.client.render.main.builders.GlProgramBuilder;
import com.sixeyes.client.render.main.compile.GlslFileEntry;
import com.sixeyes.client.render.main.program.shader.ShaderType;
import com.sixeyes.client.render.main.program.uniform.UniformType;

import java.util.HashMap;

public record GlProgramSnippet(HashMap<ShaderType, GlslFileEntry> shaders, HashMap<String, UniformType<?>> uniforms) {
    public <T> void applyTo(GlProgramBuilder<T> builder) {
        shaders.forEach((type, glslFileEntry) -> builder.shader(glslFileEntry, type));
        uniforms.forEach(builder::uniform);
    }
}