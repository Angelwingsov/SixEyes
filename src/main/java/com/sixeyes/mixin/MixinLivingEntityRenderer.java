package com.sixeyes.mixin;

import com.sixeyes.client.util.rotation.Rotation;
import com.sixeyes.client.util.rotation.RotationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState> {
    @Inject(
        method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
        at = @At("TAIL")
    )
    private void onExtractRenderState(T entity, S state, float partialTick, CallbackInfo ci) {
        if (entity == Minecraft.getInstance().player && RotationManager.INSTANCE.isActive()) {
            Rotation prev = RotationManager.INSTANCE.getPreviousServerRotation();
            Rotation curr = RotationManager.INSTANCE.getServerRotation();
            float pitch = Mth.lerp(partialTick, prev.getPitch(), curr.getPitch());
            state.xRot = state.isUpsideDown ? -pitch : pitch;
        }
    }
}
