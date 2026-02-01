package sweetie.evaware.mixin;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sweetie.evaware.Evaware;
import sweetie.evaware.api.render.Renderable;

import java.util.List;

import static sweetie.evaware.api.render.RenderUtil.*;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/fog/FogRenderer;rotate()V", shift = At.Shift.BEFORE))
    public void modifyRenderBeforeGui(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        if (Evaware.initRender()) return;

        List<Renderable> snapshot = LAYER_CONTROL.layers;
        snapshot.forEach(Renderable::load);

        MATRIX_CONTROL.unscaledProjection();

        GlStateManager._disableDepthTest();
        RenderSystem.resetTextureMatrix();

        Evaware.renderTest();

        MATRIX_CONTROL.scaledProjection();
    }
}
