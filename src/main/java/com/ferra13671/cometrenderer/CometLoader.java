package com.ferra13671.cometrenderer;

import com.ferra13671.cometrenderer.builders.GlProgramBuilder;
import com.ferra13671.cometrenderer.builders.GlShaderLibraryBuilder;
import com.ferra13671.cometrenderer.compile.GlShaderLibrary;
import com.ferra13671.cometrenderer.compile.GlslFileEntry;
import com.ferra13671.cometrenderer.exceptions.ExceptionPrinter;
import com.ferra13671.cometrenderer.exceptions.impl.load.LoadGlslContentException;
import com.ferra13671.cometrenderer.exceptions.impl.load.LoadShaderLibraryContentException;
import com.ferra13671.cometrenderer.program.GlProgram;
import com.ferra13671.cometrenderer.program.GlProgramSnippet;

import java.util.function.Function;

public abstract class CometLoader<T> {
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
