package net.caramel.snare.setting;
import java.util.function.BooleanSupplier;
public final class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String id, String name, String description, boolean value) { this(id, name, description, value, () -> true); }
    public BooleanSetting(String id, String name, String description, boolean value, BooleanSupplier visible) { super(id, name, description, value, visible); }
    @Override protected Boolean normalize(Boolean candidate) { return candidate; }
}