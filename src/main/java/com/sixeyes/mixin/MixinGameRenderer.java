package com.sixeyes.mixin;

import com.sixeyes.client.listener.listeners.RenderListener;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(method = "render", at = @At("TAIL"))
    public void renderGuiPipelines(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
        RenderListener.INSTANCE.hookRenderArray(
                tickCounter.getGameTimeDeltaPartialTick(false),
                RenderListener.ALL_GUI_PIPELINES
        );
    }
}
