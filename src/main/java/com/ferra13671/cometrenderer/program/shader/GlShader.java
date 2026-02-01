package com.ferra13671.cometrenderer.program.shader;

import com.ferra13671.cometrenderer.Compilable;
import com.ferra13671.cometrenderer.compile.GlShaderLibrary;
import com.ferra13671.cometrenderer.compile.GlobalCometCompiler;
import com.ferra13671.cometrenderer.program.GlProgram;
import com.ferra13671.cometrenderer.program.compile.CompileResult;
import com.ferra13671.cometrenderer.program.compile.CompileStatus;
import com.ferra13671.cometrenderer.program.uniform.UniformType;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL20;

import java.io.Closeable;
import java.util.HashMap;

public class GlShader implements Compilable, Closeable {
    
    private final String name;
    
    private final String content;
    
    private final int id;
    
    private final HashMap<String, UniformType<?>> extraUniforms;
    
    private final ShaderType shaderType;

    
    public GlShader(String name, String content, int id, HashMap<String, UniformType<?>> extraUniforms, ShaderType shaderType) {
        this.name = name;
        this.content = content;
        this.id = id;
        this.extraUniforms = extraUniforms;
        this.shaderType = shaderType;
    }

    @Override
    public void close() {
        GL20.glDeleteShader(getId());
        this.extraUniforms.clear();
    }

    @Override
    public CompileResult getCompileResult() {
        CompileStatus status = CompileStatus.fromStatusId(GL20.glGetShaderi(getId(), GL20.GL_COMPILE_STATUS));
        return new CompileResult(
                status,
                status == CompileStatus.FAILURE ? StringUtils.trim(GL20.glGetShaderInfoLog(getId())) : ""
        );
    }

    
    public String getName() {
        return name;
    }

    
    public String getContent() {
        return content;
    }

    
    public int getId() {
        return id;
    }

    
    public HashMap<String, UniformType<?>> getExtraUniforms() {
        return extraUniforms;
    }

    
    public ShaderType getShaderType() {
        return shaderType;
    }
}
