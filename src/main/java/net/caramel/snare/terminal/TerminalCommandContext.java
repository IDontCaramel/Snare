package net.caramel.snare.terminal;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
public record TerminalCommandContext(MinecraftClient client, Consumer<String> output) {
    public TerminalCommandContext {
        Objects.requireNonNull(client);
        Objects.requireNonNull(output);
    }
}
