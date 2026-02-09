package com.sixeyes.client.render.main.program;

import com.sixeyes.client.render.main.Bindable;
import com.sixeyes.client.render.main.Compilable;
import com.sixeyes.client.render.main.exceptions.ExceptionPrinter;
import com.sixeyes.client.render.main.exceptions.impl.NoSuchUniformException;
import com.sixeyes.client.render.main.program.compile.CompileResult;
import com.sixeyes.client.render.main.program.compile.CompileStatus;
import com.sixeyes.client.render.main.program.uniform.GlUniform;
import com.sixeyes.client.render.main.program.uniform.UniformType;
import com.sixeyes.client.render.main.program.uniform.uniforms.buffer.BufferUniform;
import com.sixeyes.client.render.main.program.uniform.uniforms.sampler.SamplerUniform;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL20;

import java.io.Closeable;
import java.util.*;
import java.util.function.Consumer;

public class GlProgram implements Bindable, Compilable, Closeable {
    public static GlProgram ACTIVE_PROGRAM = null;

    public final String name;
    public final int id;
    public final HashSet<GlProgramSnippet> snippets;
    private final HashMap<String, GlUniform> uniformsByName = new HashMap<>();
    private final List<SamplerUniform> samplers = new ArrayList<>();
    public int samplersAmount = 0;
    public int buffersIndexAmount = 0;
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
        CompileStatus status = CompileStatus.fromStatusId(GL20.glGetProgrami(id, GL20.GL_LINK_STATUS));
        return new CompileResult(
                status,
                status == CompileStatus.FAILURE ? StringUtils.trim(GL20.glGetProgramInfoLog(buffersIndexAmount)) : ""
        );
    }

    @Override
    public void close() {
        GL20.glDeleteProgram(id);
        this.uniformsByName.clear();
        this.samplers.clear();
    }

    @Override
    public void bind() {
        if (ACTIVE_PROGRAM != this) GL20.glUseProgram(id);

        if (!this.updatedUniforms.isEmpty()) {
            for (GlUniform glUniform : this.updatedUniforms)
                glUniform.upload();
            this.updatedUniforms.clear();
        }

        ACTIVE_PROGRAM = this;
    }

    
    @Override
    public void unbind() {
        if (ACTIVE_PROGRAM == this) GL20.glUseProgram(0);

        ACTIVE_PROGRAM = null;
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
}