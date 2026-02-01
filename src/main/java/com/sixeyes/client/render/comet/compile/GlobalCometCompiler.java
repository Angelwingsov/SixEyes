package com.sixeyes.client.render.comet.compile;

import com.sixeyes.client.render.comet.Pair;
import com.sixeyes.client.render.comet.exceptions.ExceptionPrinter;
import com.sixeyes.client.render.comet.exceptions.impl.DoubleUniformAdditionException;
import com.sixeyes.client.render.comet.exceptions.impl.NoSuchShaderLibraryException;
import com.sixeyes.client.render.comet.exceptions.impl.compile.CompileProgramException;
import com.sixeyes.client.render.comet.exceptions.impl.compile.CompileShaderException;
import com.sixeyes.client.render.comet.program.GlProgram;
import com.sixeyes.client.render.comet.program.GlProgramSnippet;
import com.sixeyes.client.render.comet.program.compile.CompileResult;
import com.sixeyes.client.render.comet.program.shader.GlShader;
import com.sixeyes.client.render.comet.program.shader.ShaderType;
import com.sixeyes.client.render.comet.program.uniform.UniformType;
import org.lwjgl.opengl.GL20;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GlobalCometCompiler {
    
    private static final String includeLibOperator = "#include";
    
    private static final HashMap<String, GlShaderLibrary> libraries = new HashMap<>();

    
    public static void registerShaderLibraries(GlShaderLibrary... shaderLibraries) {
        for (GlShaderLibrary shaderLibrary : shaderLibraries)
            libraries.put(shaderLibrary.libraryEntry().name(), shaderLibrary);
    }

    
    public static void unregisterShaderLibraries(GlShaderLibrary... shaderLibraries) {
        for (GlShaderLibrary shaderLibrary : shaderLibraries)
            libraries.remove(shaderLibrary.libraryEntry().name());
    }

    
    public static void unregisterShaderLibraries(String... names) {
        for (String name : names)
            libraries.remove(name);
    }

    
    public static GlShaderLibrary getShaderLibrary(String name) {
        GlShaderLibrary library = libraries.get(name);
        if (library == null)
            ExceptionPrinter.printAndExit(new NoSuchShaderLibraryException(name));
        return library;
    }

    
    public static GlProgram compileProgram(String name, List<GlShader> shaders, GlProgramSnippet[] snippets, HashMap<String, UniformType<?>> uniforms) {

        int programId = GL20.glCreateProgram();

        HashMap<String, UniformType<?>> allUniforms = new HashMap<>(uniforms);


        for (GlShader shader : shaders) {
            GL20.glAttachShader(programId, shader.getId());

            shader.getExtraUniforms().forEach((s, uniformType) -> {
                if (allUniforms.containsKey(s))
                    ExceptionPrinter.printAndExit(new DoubleUniformAdditionException(s));

                allUniforms.put(s, uniformType);
            });
        }


        GL20.glLinkProgram(programId);


        GlProgram program = new GlProgram(name, programId, new HashSet<>(Arrays.asList(snippets)), allUniforms);


        CompileResult compileResult = program.getCompileResult();


        if (compileResult.isFailure())
            ExceptionPrinter.printAndExit(new CompileProgramException(name, compileResult.message()));

        return program;
    }

    
    public static GlShader compileShader(GlslFileEntry shaderEntry, ShaderType shaderType) {

        Pair<String, HashMap<String, UniformType<?>>> content = includeShaderLibraries(shaderEntry.content());


        int shaderId = GL20.glCreateShader(shaderType.glId);

        GL20.glShaderSource(shaderId, content.left());

        GL20.glCompileShader(shaderId);


        GlShader shader = new GlShader(shaderEntry.name(), content.left(), shaderId, content.right(), shaderType);


        CompileResult compileResult = shader.getCompileResult();


        if (compileResult.isFailure())
            ExceptionPrinter.printAndExit(new CompileShaderException(shaderEntry.name(), compileResult.message()));

        return shader;
    }

    
    public static Pair<String, HashMap<String, UniformType<?>>> includeShaderLibraries(String content) {
        HashMap<String, UniformType<?>> uniforms = new HashMap<>();

        boolean writeAction = false;
        boolean writeLibName = false;
        StringBuilder s = new StringBuilder();
        int i = 0;
        while (i < content.length()){
            char ch = content.charAt(i);
            i++;

            if (ch == '#') {
                s = new StringBuilder("#");
                writeAction = true;
                continue;
            }
            if (ch == '<' && writeAction) {
                writeAction = false;
                if (s.toString().equals(includeLibOperator)) {
                    writeLibName = true;
                }
                s = new StringBuilder();
                continue;
            }
            if (ch == '>' && writeLibName) {
                writeLibName = false;

                GlShaderLibrary library = getShaderLibrary(s.toString());

                library.uniforms().forEach((s1, uniformType) -> {
                    if (uniforms.containsKey(s1))
                        ExceptionPrinter.printAndExit(new DoubleUniformAdditionException(s1));

                    uniforms.put(s1, uniformType);
                });

                content = content.replace(includeLibOperator.concat("<").concat(s.toString()).concat(">"), library.libraryEntry().content());
                i -= "#".concat(includeLibOperator).concat("<").concat(s.toString()).length();
                s = new StringBuilder();
                continue;
            }

            s.append(ch);
        }

        return new Pair<>(content, uniforms);
    }
}

