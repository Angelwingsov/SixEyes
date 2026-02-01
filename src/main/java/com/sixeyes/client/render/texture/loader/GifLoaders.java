package com.sixeyes.client.render.texture.loader;

import com.sixeyes.client.render.texture.PathMode;
import com.sixeyes.client.render.texture.builder.GLGifInfo;
import com.sixeyes.client.render.texture.texture.ColorMode;
import com.sixeyes.client.render.texture.texture.TextureFiltering;
import com.sixeyes.client.render.texture.texture.TextureWrapping;
import com.sixeyes.client.render.texture.utils.gif.GifData;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;


public final class GifLoaders {
    public static Function<InputStream, GifLoader> INPUT_STREAM = inputStream -> decompileMode -> {
        ImageInputStream iis = ImageIO.createImageInputStream(inputStream);
        GifData gifData = decompileMode.gifDecompiler.decompile(iis);
        return new GLGifInfo(gifData.images.stream().map(bufferedImage -> {
            try {
                return TextureLoaders.BUFFERED_IMAGE.load(bufferedImage, ColorMode.RGBA, TextureFiltering.DEFAULT, TextureWrapping.DEFAULT);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()), gifData.updateDelay);
    };
    public static BiFunction<String, PathMode, GifLoader> PATH = (path, pathMode) -> INPUT_STREAM.apply(pathMode.streamCreateFunction.apply(path));
    public static Function<URL, GifLoader> URL = url -> {
        try {
            return INPUT_STREAM.apply(url.openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };
}


