package net.caramel.snare.setting;
public final class TextSetting extends Setting<String> {
    public TextSetting(String id, String name, String description, String value) { super(id, name, description, value, () -> true); }
    @Override protected String normalize(String candidate) { return candidate; }
}