package net.caramel.snare.mixin.behavior;

import net.caramel.snare.SnareClient;
import net.caramel.snare.event.type.CancelBlockBreakingEvent;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Inject(method = "cancelBlockBreaking", at = @At("HEAD"), cancellable = true)
    private void snare$onCancelBlockBreaking(CallbackInfo ci) {
        CancelBlockBreakingEvent event = new CancelBlockBreakingEvent();
        SnareClient.events().post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
