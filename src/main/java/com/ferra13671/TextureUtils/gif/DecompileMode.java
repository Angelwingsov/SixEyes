package com.ferra13671.TextureUtils.gif;

import com.ferra13671.TextureUtils.utils.gif.GifUtils;


public enum DecompileMode {
    DELTAS(GifUtils::decompileDeltas),
    FULL(GifUtils::decompileFull);

    public final GifDecompiler gifDecompiler;

    DecompileMode(GifDecompiler gifDecompiler) {
        this.gifDecompiler = gifDecompiler;
    }
}