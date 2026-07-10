package net.caramel.snare.setting;
import java.util.List;
public final class ModeSetting extends Setting<String> {
    private final List<String> choices;
    public ModeSetting(String id, String name, String description, String value, List<String> choices) {
        super(id, name, description, validate(value, choices), () -> true); this.choices = List.copyOf(choices);
    }
    private static String validate(String value, List<String> choices) {
        if (choices == null || choices.isEmpty() || choices.stream().anyMatch(String::isBlank) || !choices.contains(value)) throw new IllegalArgumentException("invalid mode definition");
        return value;
    }
    @Override protected String normalize(String candidate) { return choices == null || !choices.contains(candidate) ? defaultValue() : candidate; }
    public List<String> choices() { return choices; }
    public void cycle(int direction) { int index = choices.indexOf(value()); setValue(choices.get(Math.floorMod(index + Integer.signum(direction), choices.size()))); }
}