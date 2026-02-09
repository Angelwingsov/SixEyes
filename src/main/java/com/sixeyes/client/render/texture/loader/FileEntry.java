package com.sixeyes.client.render.texture.loader;

import com.sixeyes.client.render.texture.PathMode;


public class FileEntry {
    private final String path;
    private final PathMode pathMode;

    public FileEntry(String path, PathMode pathMode) {
        this.path = path;
        this.pathMode = pathMode;
    }

    public String getPath() {
        return path;
    }

    public PathMode getPathMode() {
        return pathMode;
    }
}
