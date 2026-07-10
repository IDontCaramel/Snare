package net.caramel.snare.setting;
public final class KeybindSetting extends Setting<Integer> {
    public static final int UNBOUND = -1;
    public KeybindSetting(String id, String name, String description, int value) { super(id, name, description, value, () -> true); }
    @Override protected Integer normalize(Integer candidate) { return candidate < UNBOUND ? UNBOUND : candidate; }
}