package com.sixeyes.client.render.texture.gif;

import com.sixeyes.client.render.texture.utils.gif.GifUtils;


public enum DecompileMode {
    DELTAS(GifUtils::decompileDeltas),
    FULL(GifUtils::decompileFull);

    public final GifDecompiler gifDecompiler;

    DecompileMode(GifDecompiler gifDecompiler) {
        this.gifDecompiler = gifDecompiler;
    }
}