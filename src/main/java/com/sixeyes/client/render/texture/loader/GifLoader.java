package com.sixeyes.client.render.texture.loader;

import com.sixeyes.client.render.texture.builder.GLGifInfo;
import com.sixeyes.client.render.texture.gif.DecompileMode;


public interface GifLoader {

    GLGifInfo load(DecompileMode decompileMode) throws Exception;
}
