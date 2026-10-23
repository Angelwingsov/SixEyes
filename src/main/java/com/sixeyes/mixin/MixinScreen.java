package com.sixeyes.mixin;

import com.sixeyes.client.listener.listeners.RenderListener;
import com.sixeyes.event.EventManager;
import com.sixeyes.event.events.ScreenCloseEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class MixinScreen {
    @Inject(method = "onClose", at = @At("HEAD"), cancellable = true)
    private void onCloseHook(CallbackInfo ci) {
        if (!((Object) this instanceof AbstractContainerScreen<?>)) {
            ScreenCloseEvent event = new ScreenCloseEvent((Screen) (Object) this);
            EventManager.INSTANCE.post(event);
            if (event.getCancel()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "extractRenderStateWithTooltipAndSubtitles", at = @At("TAIL"))
    private void onExtractRenderStateTail(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        RenderListener.INSTANCE.renderVulkanScreenOverlay(guiGraphics);
    }
}
