package com.sixeyes.mixin;

import com.sixeyes.client.util.rotation.RotationManager;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput {
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickInput(CallbackInfo ci) {
        RotationManager.INSTANCE.onInputUpdate((ClientInput) (Object) this);
    }
}
