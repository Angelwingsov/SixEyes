package com.sixeyes.client.api.render;

import com.sixeyes.client.api.render.renderers.*;

public class RenderUtil {
    public static MatrixControl MATRIX_CONTROL = new MatrixControl();
    public static LayerControl LAYER_CONTROL = new LayerControl();

    public static ShadowRenderer SHADOW = new ShadowRenderer();
    public static RectRenderer RECT = new RectRenderer();
    public static KawaseRenderer KAWASE = new KawaseRenderer();
    public static TextureRectRenderer TEXTURE_RECT = new TextureRectRenderer();
    public static BlurRectRenderer BLUR_RECT = new BlurRectRenderer();
}


