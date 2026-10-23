package com.sixeyes.mixin;

import com.sixeyes.module.modules.render.TagsModule;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer<T extends Entity, S extends EntityRenderState> {
    @Inject(
        method = "extractNameTags(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FDD)V",
        at = @At("TAIL")
    )
    private void hideVanillaNameTags(T entity, S state, float partialTick, double nameTagDist, double belowNameDist, CallbackInfo ci) {
        if (TagsModule.INSTANCE.shouldHideVanillaTag(entity)) {
            state.nameTag = null;
            state.scoreText = null;
        }
    }
}
