package com.ferra13671.cometrenderer.builders;

import com.ferra13671.cometrenderer.compile.GlslFileEntry;
import com.ferra13671.cometrenderer.program.shader.ShaderType;

public record ShaderSchema(GlslFileEntry shaderEntry, ShaderType shaderType) {}
