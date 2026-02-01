package com.sixeyes.client.render.comet.program;

import com.sixeyes.client.render.comet.builders.GlProgramBuilder;
import com.sixeyes.client.render.comet.compile.GlslFileEntry;
import com.sixeyes.client.render.comet.program.shader.ShaderType;
import com.sixeyes.client.render.comet.program.uniform.UniformType;

import java.util.HashMap;

public record GlProgramSnippet(HashMap<ShaderType, GlslFileEntry> shaders, HashMap<String, UniformType<?>> uniforms) {

    
    public <T> void applyTo(GlProgramBuilder<T> builder) {
        shaders.forEach((type, glslFileEntry) -> builder.shader(glslFileEntry, type));
        uniforms.forEach(builder::uniform);
    }
}

