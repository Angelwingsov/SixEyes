package com.sixeyes.mixin;

import com.sixeyes.event.EventManager;
import com.sixeyes.event.events.KeyPressEvent;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler {
    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void onKeyPress(long window, int action, KeyEvent keyEvent, CallbackInfo ci) {
        KeyPressEvent event = new KeyPressEvent(keyEvent.key(), keyEvent.keycode(), action, keyEvent.modifiers());
        EventManager.INSTANCE.post(event);
        if (event.getCancel()) {
            ci.cancel();
        }
    }
}
