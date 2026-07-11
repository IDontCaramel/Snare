package net.caramel.snare.mixin.movement;

import net.caramel.snare.SnareClient;
import net.caramel.snare.module.tweak.CreativeElytraFlightModule;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "startFallFlying", at = @At("TAIL"))
    private void snare$creativeElytraFlight(CallbackInfo ci) {
        if (!((Object) this instanceof ClientPlayerEntity player)) return;
        CreativeElytraFlightModule module = SnareClient.modules()
            .get(CreativeElytraFlightModule.class).orElse(null);
        if (module != null && module.isEnabled()) module.begin(player);
    }
}
