package com.ferra13671.cometrenderer.compile;

import com.ferra13671.cometrenderer.CometLoader;
import com.ferra13671.cometrenderer.program.uniform.UniformType;

import java.util.HashMap;

public record GlShaderLibrary(GlslFileEntry libraryEntry, HashMap<String, UniformType<?>> uniforms) {
}
