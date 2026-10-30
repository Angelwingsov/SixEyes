package com.sixeyes.client.render.main.program;

import com.sixeyes.client.render.main.Bindable;
import com.sixeyes.client.render.main.Compilable;
import com.sixeyes.client.render.main.exceptions.ExceptionPrinter;
import com.sixeyes.client.render.main.exceptions.impl.NoSuchUniformException;
import com.sixeyes.client.render.main.program.compile.CompileResult;
import com.sixeyes.client.render.main.program.compile.CompileStatus;
import com.sixeyes.client.render.main.program.uniform.GlUniform;
import com.sixeyes.client.render.main.program.uniform.UniformType;
import com.sixeyes.client.render.main.program.uniform.uniforms.Matrix4fGlUniform;
import com.sixeyes.client.render.main.program.uniform.uniforms.buffer.BufferUniform;
import com.sixeyes.client.render.main.program.uniform.uniforms.sampler.SamplerUniform;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL20;

import java.io.Closeable;
import java.util.*;

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

    public BufferUniform projMatUniform;
    public Matrix4fGlUniform modelViewUniform;

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

        GlUniform proj = this.uniformsByName.get("ProjMat");
        if (proj instanceof BufferUniform bu) this.projMatUniform = bu;

        GlUniform mv = this.uniformsByName.get("ModelViewMat");
        if (mv instanceof Matrix4fGlUniform mu) this.modelViewUniform = mu;

        if (!samplers.isEmpty()) {
            GL20.glUseProgram(this.id);
            for (SamplerUniform sampler : samplers) {
                if (sampler.getLocation() != -1) {
                    GL20.glUniform1i(sampler.getLocation(), sampler.samplerId);
                }
            }
            GL20.glUseProgram(0);
        }
    }

    @Override
    public CompileResult getCompileResult() {
        CompileStatus status = CompileStatus.fromStatusId(GL20.glGetProgrami(id, GL20.GL_LINK_STATUS));
        return new CompileResult(
                status,
                status == CompileStatus.FAILURE ? StringUtils.trim(GL20.glGetProgramInfoLog(id)) : ""
        );
    }

    @Override
    public void close() {
        if (ACTIVE_PROGRAM == this) {
            unbindActive();
        }
        GL20.glDeleteProgram(id);
        this.uniformsByName.clear();
        this.samplers.clear();
    }

    @Override
    public void bind() {
        if (ACTIVE_PROGRAM != this) {
            GL20.glUseProgram(id);
            ACTIVE_PROGRAM = this;
            com.sixeyes.client.util.render.engine.dispatcher.RenderMetrics.INSTANCE.onProgramSwitch();
        }

        if (!this.updatedUniforms.isEmpty()) {
            for (int i = 0; i < this.updatedUniforms.size(); i++) {
                this.updatedUniforms.get(i).upload();
            }
            this.updatedUniforms.clear();
        }
    }

    public static void unbindActive() {
        GL20.glUseProgram(0);
        ACTIVE_PROGRAM = null;
    }

    @Override
    public void unbind() {
        unbindActive();
    }

    public void addUpdatedUniform(GlUniform uniform) {
        if (!this.updatedUniforms.contains(uniform)) {
            this.updatedUniforms.add(uniform);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends GlUniform> T getUniform(String name, UniformType<T> uniformType) {
        return (T) this.uniformsByName.get(name);
    }

    public GlUniform getUniform(String name) {
        return this.uniformsByName.get(name);
    }

    public SamplerUniform getSamplerNullable(int samplerId) {
        return this.samplers.get(samplerId);
    }
}
