package net.caramel.snare.terminal;
import java.util.List;
public interface TerminalCommand {
    String name();
    String usage();
    String description();
    void execute(String arguments, TerminalCommandContext context);
    List<TerminalSuggestion> suggest(String input);
}
