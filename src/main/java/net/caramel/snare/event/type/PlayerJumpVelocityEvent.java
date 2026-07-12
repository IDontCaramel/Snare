package net.caramel.snare.event.type;

import net.minecraft.entity.LivingEntity;
import java.util.Objects;

public final class PlayerJumpVelocityEvent {
    private final LivingEntity entity;
    private float velocity;
    public PlayerJumpVelocityEvent(LivingEntity entity, float velocity) {
        this.entity = Objects.requireNonNull(entity);
        this.velocity = velocity;
    }
    public LivingEntity entity() { return entity; }
    public float velocity() { return velocity; }
    public void velocity(float v) { this.velocity = v; }
}
