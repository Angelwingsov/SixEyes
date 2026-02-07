package com.sixeyes.client;

import com.sixeyes.client.render.comet.CometLoaders;
import com.sixeyes.client.render.comet.CometRenderer;
import com.sixeyes.client.render.comet.blend.DstFactor;
import com.sixeyes.client.render.comet.blend.SrcFactor;
import com.sixeyes.client.render.comet.compile.GlobalCometCompiler;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sixeyes.client.api.render.*;
import com.sixeyes.client.api.render.font.Fonts;
import com.sixeyes.client.mixin.IGlGpuBuffer;

import java.awt.*;
import java.util.Arrays;
import static com.sixeyes.client.api.utility.other.Mc.mc;
import static com.sixeyes.client.api.render.RenderUtil.*;

public class SixEyes implements ModInitializer {
    public static final String MOD_ID = "SixEyes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean init = false;

    public static boolean initRender() {
        if (!init) {
            LAYER_CONTROL.layers.addAll(Arrays.asList(RECT, TEXTURE_RECT, KAWASE, BLUR_RECT, SHADOW));
            LAYER_CONTROL.batchables.addAll(Arrays.asList(RECT, TEXTURE_RECT, BLUR_RECT, SHADOW));
            LAYER_CONTROL.batchables.addAll(Fonts.getFonts());

            init = true;
            return true;
        }

        return false;
    }

    public static void renderTest() {
        KAWASE.applyBlur();
        //test commit (johon0error)
        int interacts = 10;
        float size = 67f;
        float x = 3f;
        float y = 3f;
        float round = size * 0.2f;

        CometRenderer.bindMainFramebuffer();
        CometRenderer.applyBlend(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ONE, DstFactor.ZERO);

        LAYER_CONTROL.batchables.forEach(Batchable::begin);
        Fonts.SF_SEMIBOLD.priority(RenderPipeline.HUD).drawText("fps: " + mc.getCurrentFps() + " | draws: " + interacts, 3f, 3f, 19f, Color.WHITE);

        y += size;
        BLUR_RECT.priority(RenderPipeline.BACKGROUND).draw(x, y, size, size, Color.WHITE, round, 0.1f);

        SHADOW.priority(RenderPipeline.GAME).drawShadow(x, y, size, size, new Color(0, 0, 0, 20), round, 8f);

        LAYER_CONTROL.renderAll();

        LAYER_CONTROL.batchables.forEach(Batchable::end);
    }

    @Override
    public void onInitialize() {
        CometRenderer.init(glGpuBuffer -> ((IGlGpuBuffer) glGpuBuffer)._getId(), () -> mc.getWindow().getScaleFactor());

        String include = Renderable.SHADER_INCLUDE_PATH;

        GlobalCometCompiler.registerShaderLibraries(
                CometLoaders.IN_JAR.createShaderLibraryBuilder()
                        .name("math_utils")
                        .library(include + "math_utils.glsl")
                        .build(),
                CometLoaders.IN_JAR.createShaderLibraryBuilder()
                        .name("shapes")
                        .library(include + "shapes.glsl")
                        .build());

        LOGGER.info("SixEyes initialized!");
    }
}
