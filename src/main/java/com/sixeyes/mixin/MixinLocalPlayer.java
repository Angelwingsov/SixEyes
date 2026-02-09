package com.sixeyes.mixin;

import com.sixeyes.event.EventManager;
import com.sixeyes.event.events.PlayerUpdateEvent;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer {
    @Inject(method = "tick", at = @At("HEAD"))
    public void tickHook(CallbackInfo ci) {
        EventManager.INSTANCE.post(PlayerUpdateEvent.INSTANCE);
    }
}
