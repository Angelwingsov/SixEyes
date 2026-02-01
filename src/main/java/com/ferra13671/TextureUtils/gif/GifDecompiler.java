package com.ferra13671.TextureUtils.gif;

import com.ferra13671.TextureUtils.utils.gif.GifData;

import javax.imageio.stream.ImageInputStream;


public interface GifDecompiler {

    GifData decompile(ImageInputStream iis) throws Exception;
}
