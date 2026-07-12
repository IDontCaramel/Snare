package net.caramel.snare.event.type;

import net.minecraft.entity.Entity;
import java.util.Objects;

public final class SaddleCheckEvent {
    private final Entity entity;
    private boolean saddled;
    public SaddleCheckEvent(Entity entity, boolean saddled) {
        this.entity = Objects.requireNonNull(entity);
        this.saddled = saddled;
    }
    public Entity entity() { return entity; }
    public boolean saddled() { return saddled; }
    public void saddled(boolean v) { this.saddled = v; }
}
