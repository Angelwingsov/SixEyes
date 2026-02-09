package com.sixeyes.client.render.main.compile;

import com.sixeyes.client.render.main.program.uniform.UniformType;

import java.util.HashMap;

public record GlShaderLibrary(GlslFileEntry libraryEntry, HashMap<String, UniformType<?>> uniforms) {
}
