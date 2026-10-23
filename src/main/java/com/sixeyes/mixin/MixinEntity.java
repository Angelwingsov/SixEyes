package com.sixeyes.mixin;

import com.sixeyes.client.util.rotation.RotationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class MixinEntity {
    @Redirect(
        method = "moveRelative(FLnet/minecraft/world/phys/Vec3;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getYRot()F")
    )
    private float redirectMoveRelativeYaw(Entity instance) {
        if (instance == Minecraft.getInstance().player) {
            return RotationManager.INSTANCE.getMovementYaw(instance.getYRot());
        }
        return instance.getYRot();
    }
}
