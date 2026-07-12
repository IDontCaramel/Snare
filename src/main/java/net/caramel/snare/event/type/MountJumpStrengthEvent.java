package net.caramel.snare.event.type;

import net.minecraft.client.network.ClientPlayerEntity;
import java.util.Objects;

public final class MountJumpStrengthEvent {
    private final ClientPlayerEntity player;
    private float strength;
    public MountJumpStrengthEvent(ClientPlayerEntity player, float strength) {
        this.player = Objects.requireNonNull(player);
        this.strength = strength;
    }
    public ClientPlayerEntity player() { return player; }
    public float strength() { return strength; }
    public void strength(float v) { this.strength = v; }
}
