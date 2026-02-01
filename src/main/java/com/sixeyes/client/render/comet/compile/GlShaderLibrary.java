package com.sixeyes.client.render.comet.compile;

import com.sixeyes.client.render.comet.CometLoader;
import com.sixeyes.client.render.comet.program.uniform.UniformType;

import java.util.HashMap;

public record GlShaderLibrary(GlslFileEntry libraryEntry, HashMap<String, UniformType<?>> uniforms) {
}


