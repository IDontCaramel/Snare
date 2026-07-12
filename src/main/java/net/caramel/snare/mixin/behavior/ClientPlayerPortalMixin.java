package net.caramel.snare.mixin.behavior;

import net.caramel.snare.SnareClient;
import net.caramel.snare.event.type.PortalScreenCheckEvent;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerPortalMixin {
    @Redirect(method = "updateNausea", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;shouldPause()Z"))
    private boolean snare$redirectShouldPause(Screen screen) {
        boolean original = screen.shouldPause();
        PortalScreenCheckEvent event = new PortalScreenCheckEvent(original);
        SnareClient.events().post(event);
        return event.blocksPortalClose();
    }
}
