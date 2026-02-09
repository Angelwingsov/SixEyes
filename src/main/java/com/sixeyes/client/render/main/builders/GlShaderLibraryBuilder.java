package com.sixeyes.client.render.main.builders;

import com.sixeyes.client.render.main.compile.GlShaderLibrary;
import com.sixeyes.client.render.main.compile.GlslFileEntry;
import com.sixeyes.client.render.main.exceptions.ExceptionPrinter;
import com.sixeyes.client.render.main.exceptions.impl.DoubleUniformAdditionException;
import com.sixeyes.client.render.main.exceptions.impl.IllegalLibraryBuilderArgumentException;
import com.sixeyes.client.render.main.program.GlProgramSnippet;
import com.sixeyes.client.render.main.program.uniform.GlUniform;
import com.sixeyes.client.render.main.program.uniform.UniformType;

import java.util.HashMap;
import java.util.function.Function;

public class GlShaderLibraryBuilder<T> {
    private String name;
    private T libraryPath;
    private final HashMap<String, UniformType<?>> uniforms = new HashMap<>();
    private final Function<T, String> contentGetter;

    public GlShaderLibraryBuilder(Function<T, String> contentGetter, GlProgramSnippet... snippets) {
        for (GlProgramSnippet snippet : snippets)
            snippet.uniforms().forEach(this::uniform);

        this.contentGetter = contentGetter;
    }

    public GlShaderLibraryBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public GlShaderLibraryBuilder<T> library(T libraryPath) {
        this.libraryPath = libraryPath;
        return this;
    }

    public <S extends GlUniform> GlShaderLibraryBuilder<T> uniform(String name, UniformType<S> uniformType) {
        if (this.uniforms.containsKey(name))
            ExceptionPrinter.printAndExit(new DoubleUniformAdditionException(name));

        this.uniforms.put(name, uniformType);
        return this;
    }

    public GlShaderLibraryBuilder<T> sampler(String name) {
        uniform(name, UniformType.SAMPLER);
        return this;
    }

    public GlShaderLibrary build() {
        if (name == null)
            ExceptionPrinter.printAndExit(new IllegalLibraryBuilderArgumentException("Missing name in shader library builder."));
        if (libraryPath == null)
            ExceptionPrinter.printAndExit(new IllegalLibraryBuilderArgumentException(String.format("Missing libraryPath in library '%s'.", name)));

        return new GlShaderLibrary(
                new GlslFileEntry(
                        name,
                        contentGetter.apply(libraryPath)
                ),
                uniforms
        );
    }
}
