package com.sixeyes.client.render.comet.builders;

import com.sixeyes.client.render.comet.compile.GlShaderLibrary;
import com.sixeyes.client.render.comet.compile.GlslFileEntry;
import com.sixeyes.client.render.comet.exceptions.ExceptionPrinter;
import com.sixeyes.client.render.comet.exceptions.impl.DoubleUniformAdditionException;
import com.sixeyes.client.render.comet.exceptions.impl.IllegalLibraryBuilderArgumentException;
import com.sixeyes.client.render.comet.program.GlProgramSnippet;
import com.sixeyes.client.render.comet.program.uniform.GlUniform;
import com.sixeyes.client.render.comet.program.uniform.UniformType;

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


