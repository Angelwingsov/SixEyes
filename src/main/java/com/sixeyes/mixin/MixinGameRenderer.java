package com.sixeyes.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.sixeyes.client.listener.listeners.RenderListener;

import static com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline.*;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V", shift = At.Shift.AFTER))
    public void renderGuiPipelines(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
        RenderListener.INSTANCE.hookRenderArray(
                tickCounter.getGameTimeDeltaPartialTick(false),
                RenderListener.ALL_GUI_PIPELINES
        );
    }
}
