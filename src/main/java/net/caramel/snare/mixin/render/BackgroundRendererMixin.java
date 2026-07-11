package net.caramel.snare.mixin.render;

import net.caramel.snare.SnareClient;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.tweak.FogControlModule;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void snare$applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float partialTicks, CallbackInfo ci) {
        if (!SnareClient.modules().get(FogControlModule.class).filter(Module::isEnabled).isPresent()) {
            return;
        }
        float start;
        float end;
        FogShape shape = FogShape.SPHERE;
        if (thickFog) {
            start = viewDistance * 0.05F;
            end = Math.min(viewDistance, 192.0F) * 0.5F;
        } else if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
            start = 0.0F;
            end = viewDistance;
            shape = FogShape.CYLINDER;
        } else {
            float margin = MathHelper.clamp(viewDistance / 10.0F, 4.0F, 64.0F);
            start = viewDistance - margin;
            end = viewDistance;
            shape = FogShape.CYLINDER;
        }
        RenderSystem.setShaderFogStart(start);
        RenderSystem.setShaderFogEnd(end);
        RenderSystem.setShaderFogShape(shape);
        ci.cancel();
    }
}