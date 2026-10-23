package com.sixeyes.mixin;

import com.sixeyes.event.EventManager;
import com.sixeyes.event.events.ScreenCloseEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen {
    @Inject(method = "onClose", at = @At("HEAD"), cancellable = true)
    private void onCloseHook(CallbackInfo ci) {
        ScreenCloseEvent event = new ScreenCloseEvent((Screen) (Object) this);
        EventManager.INSTANCE.post(event);
        if (event.getCancel()) {
            ci.cancel();
        }
    }
}
