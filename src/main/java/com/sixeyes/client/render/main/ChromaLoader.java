package com.sixeyes.client.render.main;

import com.sixeyes.client.render.main.builders.GlProgramBuilder;
import com.sixeyes.client.render.main.builders.GlShaderLibraryBuilder;
import com.sixeyes.client.render.main.compile.GlslFileEntry;
import com.sixeyes.client.render.main.exceptions.ExceptionPrinter;
import com.sixeyes.client.render.main.exceptions.impl.load.LoadGlslContentException;
import com.sixeyes.client.render.main.exceptions.impl.load.LoadShaderLibraryContentException;
import com.sixeyes.client.render.main.program.GlProgramSnippet;

import java.util.function.Function;

public abstract class ChromaLoader<T> {
    private final Function<T, String> glslContentGetter = path -> {
        String content = null;
        try {
            content = getContent(path);
        } catch (Exception e) {
            ExceptionPrinter.printAndExit(new LoadGlslContentException(e));
        }
        return content;
    };
    private final Function<T, String> shaderLibraryContentGetter = path -> {
        String content = null;
        try {
            content = getContent(path);
        } catch (Exception e) {
            ExceptionPrinter.printAndExit(new LoadShaderLibraryContentException(e));
        }
        return content;
    };

    public GlProgramBuilder<T> createProgramBuilder(GlProgramSnippet... snippets) {
        return new GlProgramBuilder<>(this, snippets);
    }

    public GlShaderLibraryBuilder<T> createShaderLibraryBuilder(GlProgramSnippet... snippets) {
        return new GlShaderLibraryBuilder<>(shaderLibraryContentGetter, snippets);
    }

    public GlslFileEntry createGlslFileEntry(String name, T path) {
        return new GlslFileEntry(name, glslContentGetter.apply(path));
    }

    public abstract String getContent(T path) throws Exception;
}
