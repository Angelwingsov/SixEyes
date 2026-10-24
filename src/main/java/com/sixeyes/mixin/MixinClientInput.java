package com.sixeyes.mixin;

import com.sixeyes.client.util.rotation.MoveFixProcessor;
import com.sixeyes.client.util.rotation.RotationManager;
import net.minecraft.client.player.ClientInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientInput.class)
public class MixinClientInput {
    @Inject(method = "hasForwardImpulse", at = @At("HEAD"), cancellable = true)
    private void onHasForwardImpulse(CallbackInfoReturnable<Boolean> cir) {
        if (RotationManager.INSTANCE.isActive() && MoveFixProcessor.INSTANCE.shouldOverrideForwardImpulse((ClientInput) (Object) this)) {
            cir.setReturnValue(true);
        }
    }
}
