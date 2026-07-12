package net.caramel.snare.terminal;
import java.util.Objects;
public record TerminalSuggestion(String replacement, String label, String description) {
    public TerminalSuggestion {
        Objects.requireNonNull(label);
        Objects.requireNonNull(description);
    }
    public boolean completable() { return replacement != null; }
}
