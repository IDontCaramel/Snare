package net.caramel.snare.mixin.movement;

import net.caramel.snare.SnareClient;
import net.caramel.snare.module.tweak.JumpVelocityModule;
import net.caramel.snare.module.tweak.StepHeightModule;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "getJumpVelocityMultiplier", at = @At("HEAD"), cancellable = true)
    private void snare$jumpVelocity(CallbackInfoReturnable<Float> cir) {
        if (!((Object) this instanceof ClientPlayerEntity)) return;
        JumpVelocityModule module = SnareClient.modules().get(JumpVelocityModule.class).orElse(null);
        if (module != null && module.isEnabled()) cir.setReturnValue(module.velocity.value().floatValue());
    }

    @Inject(method = "getStepHeight", at = @At("HEAD"), cancellable = true)
    private void snare$stepHeight(CallbackInfoReturnable<Float> cir) {
        if (!((Object) this instanceof ClientPlayerEntity)) return;
        StepHeightModule module = SnareClient.modules().get(StepHeightModule.class).orElse(null);
        if (module != null && module.isEnabled()) cir.setReturnValue(module.height.value().floatValue());
    }
}
