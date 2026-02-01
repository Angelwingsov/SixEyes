package com.ferra13671.cometrenderer.program;

import com.ferra13671.cometrenderer.builders.GlProgramBuilder;
import com.ferra13671.cometrenderer.compile.GlslFileEntry;
import com.ferra13671.cometrenderer.program.shader.ShaderType;
import com.ferra13671.cometrenderer.program.uniform.UniformType;

import java.util.HashMap;

public record GlProgramSnippet(HashMap<ShaderType, GlslFileEntry> shaders, HashMap<String, UniformType<?>> uniforms) {

    
    public <T> void applyTo(GlProgramBuilder<T> builder) {
        shaders.forEach((type, glslFileEntry) -> builder.shader(glslFileEntry, type));
        uniforms.forEach(builder::uniform);
    }
}