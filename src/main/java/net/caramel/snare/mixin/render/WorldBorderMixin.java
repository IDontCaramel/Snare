package net.caramel.snare.mixin.render;

import net.caramel.snare.SnareClient;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.tweak.NoWorldBorderModule;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldBorder.class)
public class WorldBorderMixin {
    @Inject(method = "getBoundWest", at = @At("HEAD"), cancellable = true)
    private void snare$unboundedWest(CallbackInfoReturnable<Double> cir) {
        if (SnareClient.modules().get(NoWorldBorderModule.class).filter(Module::isEnabled).isPresent()) {
            cir.setReturnValue(-Double.MAX_VALUE);
        }
    }

    @Inject(method = "getBoundNorth", at = @At("HEAD"), cancellable = true)
    private void snare$unboundedNorth(CallbackInfoReturnable<Double> cir) {
        if (SnareClient.modules().get(NoWorldBorderModule.class).filter(Module::isEnabled).isPresent()) {
            cir.setReturnValue(-Double.MAX_VALUE);
        }
    }

    @Inject(method = "getBoundEast", at = @At("HEAD"), cancellable = true)
    private void snare$unboundedEast(CallbackInfoReturnable<Double> cir) {
        if (SnareClient.modules().get(NoWorldBorderModule.class).filter(Module::isEnabled).isPresent()) {
            cir.setReturnValue(Double.MAX_VALUE);
        }
    }

    @Inject(method = "getBoundSouth", at = @At("HEAD"), cancellable = true)
    private void snare$unboundedSouth(CallbackInfoReturnable<Double> cir) {
        if (SnareClient.modules().get(NoWorldBorderModule.class).filter(Module::isEnabled).isPresent()) {
            cir.setReturnValue(Double.MAX_VALUE);
        }
    }

    @Inject(method = "getSize", at = @At("HEAD"), cancellable = true)
    private void snare$unboundedSize(CallbackInfoReturnable<Double> cir) {
        if (SnareClient.modules().get(NoWorldBorderModule.class).filter(Module::isEnabled).isPresent()) {
            cir.setReturnValue(Double.MAX_VALUE);
        }
    }
}