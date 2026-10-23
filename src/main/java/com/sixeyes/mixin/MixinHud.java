package com.sixeyes.mixin;

import com.sixeyes.client.listener.listeners.RenderListener;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class MixinHud {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void renderSixEyesHud(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        RenderListener.INSTANCE.renderVulkanHud(guiGraphics, deltaTracker);
    }
}
