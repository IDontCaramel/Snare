package net.caramel.snare.event.type;

import net.caramel.snare.event.CancellableEvent;
import net.minecraft.network.packet.Packet;

public final class PacketSendEvent extends CancellableEvent {
    private final Packet<?> packet;

    public PacketSendEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> packet() {
        return packet;
    }
}
