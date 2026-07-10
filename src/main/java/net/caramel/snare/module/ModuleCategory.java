package net.caramel.snare.module;
import java.util.Objects;
public record ModuleCategory(String id, String name) {
    public ModuleCategory { if (id == null || id.isBlank() || name == null || name.isBlank()) throw new IllegalArgumentException("category fields must not be blank"); }
}