package net.caramel.snare.event.type;

import net.minecraft.entity.Entity;
import java.util.Objects;

public final class EntityInterpolationEvent {
    private final Entity entity;
    private int steps;
    public EntityInterpolationEvent(Entity entity, int steps) {
        this.entity = Objects.requireNonNull(entity);
        this.steps = steps;
    }
    public Entity entity() { return entity; }
    public int steps() { return steps; }
    public void steps(int v) { this.steps = v; }
}
