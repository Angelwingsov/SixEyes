package com.ferra13671.TextureUtils.utils.gif;

import java.awt.image.BufferedImage;
import java.util.List;


public class GifData {
    public final List<BufferedImage> images;
    public final int updateDelay;

    public GifData(List<BufferedImage> images, int updateDelay) {
        this.images = images;
        this.updateDelay = updateDelay;
    }
}
