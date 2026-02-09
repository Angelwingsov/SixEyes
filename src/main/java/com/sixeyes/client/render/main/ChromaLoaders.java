package com.sixeyes.client.render.main;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public final class ChromaLoaders {
    public static final ChromaLoader<String> IN_JAR = new ChromaLoader<>() {
        @Override
        public String getContent(String path) throws Exception {
            InputStream inputStream = ChromaLoaders.class.getClassLoader().getResourceAsStream(path);
            String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            inputStream.close();
            return content;
        }
    };

    public static final ChromaLoader<InputStream> INPUT_STREAM = new ChromaLoader<>() {
        @Override
        public String getContent(InputStream path) throws Exception {
            String content = IOUtils.toString(path, StandardCharsets.UTF_8);
            path.close();
            return content;
        }
    };

    public static final ChromaLoader<URI> URI = new ChromaLoader<>() {
        @Override
        public String getContent(URI path) throws Exception {
            InputStream inputStream = path.toURL().openStream();
            String content = IOUtils.toString(path, StandardCharsets.UTF_8);
            inputStream.close();
            return content;
        }
    };
}
