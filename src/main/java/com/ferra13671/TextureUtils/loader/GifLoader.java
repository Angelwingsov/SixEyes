package com.ferra13671.TextureUtils.loader;

import com.ferra13671.TextureUtils.builder.GLGifInfo;
import com.ferra13671.TextureUtils.gif.DecompileMode;


public interface GifLoader {

    GLGifInfo load(DecompileMode decompileMode) throws Exception;
}
