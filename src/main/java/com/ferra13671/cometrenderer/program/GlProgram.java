package com.ferra13671.cometrenderer.program;

import com.ferra13671.cometrenderer.Bindable;
import com.ferra13671.cometrenderer.Compilable;
import com.ferra13671.cometrenderer.compile.GlobalCometCompiler;
import com.ferra13671.cometrenderer.exceptions.ExceptionPrinter;
import com.ferra13671.cometrenderer.exceptions.impl.NoSuchUniformException;
import com.ferra13671.cometrenderer.program.compile.CompileResult;
import com.ferra13671.cometrenderer.program.compile.CompileStatus;
import com.ferra13671.cometrenderer.program.shader.GlShader;
import com.ferra13671.cometrenderer.program.uniform.GlUniform;
import com.ferra13671.cometrenderer.program.uniform.UniformType;
import com.ferra13671.cometrenderer.program.uniform.uniforms.OneTypeGlUniform;
import com.ferra13671.cometrenderer.program.uniform.uniforms.buffer.BufferUniform;
import com.ferra13671.cometrenderer.program.uniform.uniforms.sampler.SamplerUniform;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL20;

import java.io.Closeable;
import java.util.*;
import java.util.function.Consumer;

public class GlProgram implements Bindable, Compilable, Closeable {
    public static GlProgram ACTIVE_PROGRAM = null;

    
    private final String name;
    
    private final int id;
    
    private final HashSet<GlProgramSnippet> snippets;
    
    private final HashMap<String, GlUniform> uniformsByName = new HashMap<>();
    
    private final List<SamplerUniform> samplers = new ArrayList<>();
    
    private int samplersAmount = 0;
    
    private int buffersIndexAmount = 0;
    
    private final List<GlUniform> updatedUniforms = new ArrayList<>();

    
    public GlProgram(String name, int id, HashSet<GlProgramSnippet> snippets, HashMap<String, UniformType<?>> uniforms) {
        this.name = name;
        this.id = id;
        this.snippets = snippets;

        for (Map.Entry<String, UniformType<?>> uniformEntry : uniforms.entrySet()) {
            GlUniform uniform = uniformEntry.getValue().uniformCreator().apply(
                    uniformEntry.getKey(),
                    GL20.glGetUniformLocation(this.id, uniformEntry.getKey()),
                    this
            );

            if (uniform.getLocation() == -1 && !(uniform instanceof BufferUniform))
                ExceptionPrinter.printAndExit(new NoSuchUniformException(uniform.getName(), this.name));

            this.uniformsByName.put(uniformEntry.getKey(), uniform);

            if (uniform instanceof SamplerUniform sampler)
                samplers.add(sampler);
        }
    }

    @Override
    public CompileResult getCompileResult() {
        CompileStatus status = CompileStatus.fromStatusId(GL20.glGetProgrami(getId(), GL20.GL_LINK_STATUS));
        return new CompileResult(
                status,
                status == CompileStatus.FAILURE ? StringUtils.trim(GL20.glGetProgramInfoLog(getBuffersIndexAmount())) : ""
        );
    }

    @Override
    public void close() {
        GL20.glDeleteProgram(getId());
        this.uniformsByName.clear();
        this.samplers.clear();
    }

    
    @Override
    public void bind() {
        GL20.glUseProgram(getId());

        if (!this.updatedUniforms.isEmpty()) {
            for (GlUniform glUniform : this.updatedUniforms)
                glUniform.upload();
            this.updatedUniforms.clear();
        }

        ACTIVE_PROGRAM = this;
    }

    
    @Override
    public void unbind() {
        GL20.glUseProgram(0);

        ACTIVE_PROGRAM = null;
    }

    
    public String getName() {
        return this.name;
    }

    
    public int getId() {
        return this.id;
    }

    
    public HashSet<GlProgramSnippet> getSnippets() {
        return this.snippets;
    }

    
    public void addUpdatedUniform(GlUniform uniform) {
        this.updatedUniforms.add(uniform);
    }

    
    public <T extends GlUniform> T getUniform(String name, UniformType<T> type) {
        T uniform = getUniformNullable(name, type);
        if (uniform == null)
            ExceptionPrinter.printAndExit(new NoSuchUniformException(name, this.name));
        return uniform;
    }

    
    public <T extends GlUniform> void consumeIfUniformPresent(String name, UniformType<T> type, Consumer<T> consumer) {
        T uniform = getUniformNullable(name, type);
        if (uniform != null)
            consumer.accept(uniform);
    }

    
    public <T extends GlUniform> T getUniformNullable(String name, UniformType<T> type) {
        return (T) uniformsByName.get(name);
    }

    
    public SamplerUniform getSampler(int samplerId) {
        SamplerUniform sampler = getSamplerNullable(samplerId);
        if (sampler == null)
            ExceptionPrinter.printAndExit(new NoSuchUniformException("Sampler[" + samplerId + "]", this.name));
        return sampler;
    }

    
    public void consumerIfSamplerPresent(int samplerId, Consumer<SamplerUniform> consumer) {
        SamplerUniform sampler = getSamplerNullable(samplerId);
        if (sampler != null)
            consumer.accept(sampler);
    }

    
    public SamplerUniform getSamplerNullable(int samplerId) {
        return this.samplers.get(samplerId);
    }

    
    public int getBuffersIndexAmount() {
        return buffersIndexAmount;
    }

    
    public void setBuffersIndexAmount(int buffersIndexAmount) {
        this.buffersIndexAmount = buffersIndexAmount;
    }

    
    public int getSamplersAmount() {
        return samplersAmount;
    }

    
    public void setSamplersAmount(int samplersAmount) {
        this.samplersAmount = samplersAmount;
    }
}