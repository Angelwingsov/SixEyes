package com.sixeyes.client.render.texture.gif;

import com.sixeyes.client.render.texture.utils.gif.GifData;

import javax.imageio.stream.ImageInputStream;


public interface GifDecompiler {

    GifData decompile(ImageInputStream iis) throws Exception;
}


