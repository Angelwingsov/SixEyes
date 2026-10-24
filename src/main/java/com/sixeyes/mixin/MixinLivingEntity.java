package com.sixeyes.mixin;

import com.sixeyes.client.util.rotation.MoveFixMode;
import com.sixeyes.client.util.rotation.Rotation;
import com.sixeyes.client.util.rotation.RotationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
    @Shadow
    public float yHeadRot;

    @Redirect(
        method = "jumpFromGround()V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getYRot()F")
    )
    private float redirectJumpYaw(LivingEntity instance) {
        if (instance == Minecraft.getInstance().player) {
            if (RotationManager.INSTANCE.isActive() &&
                (RotationManager.INSTANCE.getActiveMoveFix() == MoveFixMode.SILENT ||
                 RotationManager.INSTANCE.getActiveMoveFix() == MoveFixMode.FREE)) {
                return instance.getYRot();
            }
            return RotationManager.INSTANCE.getMovementYaw(instance.getYRot());
        }
        return instance.getYRot();
    }

    @Redirect(
        method = "tickHeadTurn(F)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getYRot()F")
    )
    private float redirectHeadTurnYaw(LivingEntity instance) {
        if (instance == Minecraft.getInstance().player && RotationManager.INSTANCE.isActive()) {
            return RotationManager.INSTANCE.getServerRotation().getYaw();
        }
        return instance.getYRot();
    }

    @Inject(
        method = "tick()V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;tickHeadTurn(F)V", shift = At.Shift.AFTER)
    )
    private void onAfterTickHeadTurn(CallbackInfo ci) {
        if ((Object) this == Minecraft.getInstance().player && RotationManager.INSTANCE.isActive()) {
            Rotation rot = RotationManager.INSTANCE.getServerRotation();
            this.yHeadRot = rot.getYaw();
        }
    }
}
