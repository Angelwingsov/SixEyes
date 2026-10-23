package com.sixeyes.mixin;

import com.sixeyes.client.listener.listeners.RenderListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(method = "render", at = @At("TAIL"))
    private void renderGuiPipelines(CallbackInfo ci) {
        RenderListener.INSTANCE.hookRenderArray(
            Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false),
            RenderListener.ALL_GUI_PIPELINES
        );
    }
}
