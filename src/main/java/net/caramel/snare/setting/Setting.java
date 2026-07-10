package net.caramel.snare.setting;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public abstract class Setting<T> {
    private final String id, name, description;
    private final T defaultValue;
    private final BooleanSupplier visible;
    private T value;
    private Runnable changeListener = () -> {};

    protected Setting(String id, String name, String description, T defaultValue, BooleanSupplier visible) {
        this.id = requireText(id, "id");
        this.name = requireText(name, "name");
        this.description = Objects.requireNonNull(description, "description");
        this.defaultValue = Objects.requireNonNull(defaultValue, "defaultValue");
        this.value = this.defaultValue;
        this.visible = Objects.requireNonNull(visible, "visible");
    }
    protected abstract T normalize(T candidate);
    public final String id() { return id; }
    public final String name() { return name; }
    public final String description() { return description; }
    public final T defaultValue() { return defaultValue; }
    public final T value() { return value; }
    public final boolean isVisible() { return visible.getAsBoolean(); }
    public final void setValue(T candidate) {
        T normalized = normalize(Objects.requireNonNull(candidate, "candidate"));
        if (!Objects.equals(value, normalized)) { value = normalized; changeListener.run(); }
    }
    public final void reset() { setValue(defaultValue); }
    public final void setChangeListener(Runnable listener) { changeListener = Objects.requireNonNull(listener, "listener"); }
    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " must not be blank");
        return value;
    }
}