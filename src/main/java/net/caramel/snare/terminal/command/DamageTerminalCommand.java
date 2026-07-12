package net.caramel.snare.terminal.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.caramel.snare.terminal.TerminalCommand;
import net.caramel.snare.terminal.TerminalCommandContext;
import net.caramel.snare.terminal.TerminalSuggestion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public final class DamageTerminalCommand implements TerminalCommand {

    private static final String[] MODES = { "ncp", "aac", "verus" };

    @Override
    public String name() {
        return "damage";
    }

    @Override
    public String usage() {
        return "damage <amount> <mode>";
    }

    @Override
    public String description() {
        return "Deals self-damage via NCP, AAC, or Verus. Server-dependent.";
    }

    @Override
    public void execute(String arguments, TerminalCommandContext context) {
        MinecraftClient client = context.client();
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        ClientPlayNetworkHandler network = player.networkHandler;
        if (network == null) return;

        String trimmed = arguments.trim();
        String[] parts = trimmed.split(" ", 2);
        if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            context.output().accept("Usage: " + usage());
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            context.output().accept("Invalid amount: " + parts[0] + ". Must be an integer 0-20.");
            return;
        }
        if (amount < 0 || amount > 20) {
            context.output().accept("Amount must be between 0 and 20.");
            return;
        }

        String mode = parts[1].toLowerCase(Locale.ROOT);
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        switch (mode) {
            case "ncp" -> {
                for (int i = 0; i < amount * 65; i++) {
                    network.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.0625, z, false));
                    network.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false));
                }
                network.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));
            }
            case "aac" -> {
                player.setVelocity(player.getVelocity().x, 5.0 * amount, player.getVelocity().z);
            }
            case "verus" -> {
                network.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false));
                network.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 3.25, z, false));
                network.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false));
                network.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));
            }
            default -> {
                context.output().accept("Unknown mode: " + mode + ". Available modes: ncp, aac, verus.");
                return;
            }
        }

        context.output().accept("Damage sent: " + amount + " HP via " + mode + ".");
    }

    @Override
    public List<TerminalSuggestion> suggest(String input) {
        List<TerminalSuggestion> result = new ArrayList<>();
        String trimmed = input.trim();
        String[] parts = trimmed.split(" ", 2);
        String amountStr = parts[0];

        if (amountStr.isEmpty()) {
            result.add(new TerminalSuggestion(null, "<amount: 0-20>", "Enter damage amount 0-20"));
            return result;
        }

        try {
            int amount = Integer.parseInt(amountStr);
            if (amount < 0 || amount > 20) {
                result.add(new TerminalSuggestion(null, "<amount: 0-20>", "Enter damage amount 0-20"));
                return result;
            }

            String modePrefix = parts.length > 1 ? parts[1].toLowerCase(Locale.ROOT) : "";
            for (String mode : MODES) {
                if (mode.startsWith(modePrefix)) {
                    String replacement = "damage " + amountStr + " " + mode;
                    result.add(new TerminalSuggestion(replacement, replacement, "Damage mode: " + mode));
                }
            }
        } catch (NumberFormatException e) {
            result.add(new TerminalSuggestion(null, "<amount: 0-20>", "Enter damage amount 0-20"));
        }

        return result;
    }
}
