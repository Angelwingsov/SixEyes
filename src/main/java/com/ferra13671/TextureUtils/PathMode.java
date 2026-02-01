package com.ferra13671.TextureUtils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;


public enum PathMode {
    INSIDE_JAR(path ->
        PathMode.class.getClassLoader().getResourceAsStream(path)
    ),
    OUTSIDE_JAR(path -> {
        try {
            return Files.newInputStream(Paths.get(path));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });

    public final Function<String, InputStream> streamCreateFunction;

    PathMode(Function<String, InputStream> streamCreateFunction) {
        this.streamCreateFunction = streamCreateFunction;
    }
}
