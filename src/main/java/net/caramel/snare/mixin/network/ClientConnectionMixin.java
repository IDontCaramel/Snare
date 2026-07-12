package net.caramel.snare.mixin.network;

import net.caramel.snare.SnareClient;
import net.caramel.snare.event.type.PacketSendEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"), cancellable = true)
    private void snare$onSend(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        PacketSendEvent event = new PacketSendEvent(packet);
        SnareClient.events().post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
