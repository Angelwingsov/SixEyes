package com.sixeyes.client.mixin;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sixeyes.client.SixEyes;
import com.sixeyes.client.api.render.Renderable;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.sixeyes.client.api.render.RenderUtil.*;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/fog/FogRenderer;rotate()V", shift = At.Shift.BEFORE))
    public void modifyRenderBeforeGui(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        if (SixEyes.initRender())
            return;

        List<Renderable> snapshot = LAYER_CONTROL.layers;
        snapshot.forEach(Renderable::load);

        MATRIX_CONTROL.unscaledProjection();

        GlStateManager._disableDepthTest();
        RenderSystem.resetTextureMatrix();

        SixEyes.renderTest();

        MATRIX_CONTROL.scaledProjection();
    }
}
