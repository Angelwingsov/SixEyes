package com.sixeyes.client.render.comet.builders;

import com.sixeyes.client.render.comet.compile.GlslFileEntry;
import com.sixeyes.client.render.comet.program.shader.ShaderType;

public record ShaderSchema(GlslFileEntry shaderEntry, ShaderType shaderType) {}


