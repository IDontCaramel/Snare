package net.caramel.snare.terminal.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.caramel.snare.terminal.TerminalCommand;
import net.caramel.snare.terminal.TerminalCommandContext;
import net.caramel.snare.terminal.TerminalSuggestion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public final class KickTerminalCommand implements TerminalCommand {

    private static final String[] MODES = {
        "quit", "invalid_packet", "self_hurt", "illegal_chat", "illegal_switch_item", "illegal_interact"
    };
    private static final String MODE_LIST = "quit, invalid_packet, self_hurt, illegal_chat, illegal_switch_item, illegal_interact";
    private final Random random = new Random();

    @Override
    public String name() {
        return "kick";
    }

    @Override
    public String usage() {
        return "kick <mode>";
    }

    @Override
    public String description() {
        return "Disconnects you in various ways. Some modes may not work on all servers.";
    }

    @Override
    public void execute(String arguments, TerminalCommandContext context) {
        MinecraftClient client = context.client();
        if (client.isInSingleplayer()) {
            context.output().accept("Cannot kick in singleplayer.");
            return;
        }
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        ClientPlayNetworkHandler network = player.networkHandler;
        if (network == null) return;

        String trimmed = arguments.trim();
        if (trimmed.isEmpty()) {
            context.output().accept("Usage: " + usage());
            context.output().accept("Modes: " + MODE_LIST);
            return;
        }

        String mode = trimmed.toLowerCase(Locale.ROOT);
        switch (mode) {
            case "quit" -> {
                context.output().accept("Disconnecting...");
                network.getConnection().disconnect(Text.empty());
            }
            case "invalid_packet" -> {
                context.output().accept("Sending invalid packet...");
                network.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                    Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !player.isOnGround()));
            }
            case "self_hurt" -> {
                context.output().accept("Attacking self...");
                network.sendPacket(PlayerInteractEntityC2SPacket.attack(player, player.isSneaking()));
            }
            case "illegal_chat" -> {
                context.output().accept("Sending illegal chat message...");
                network.sendChatMessage("\u00A7" + random.nextInt() + "\u00A7" + random.nextInt() + "\u00A7");
            }
            case "illegal_switch_item" -> {
                context.output().accept("Switching to illegal slot...");
                network.sendPacket(new UpdateSelectedSlotC2SPacket(-1));
            }
            case "illegal_interact" -> {
                context.output().accept("Interacting illegally...");
                network.sendPacket(PlayerInteractEntityC2SPacket.interactAt(
                    player, player.isSneaking(), Hand.MAIN_HAND, Vec3d.ZERO));
            }
            default -> {
                context.output().accept("Unknown mode: " + mode);
                context.output().accept("Usage: " + usage());
                context.output().accept("Modes: " + MODE_LIST);
            }
        }
    }

    @Override
    public List<TerminalSuggestion> suggest(String input) {
        List<TerminalSuggestion> result = new ArrayList<>();
        String prefix = input.trim().toLowerCase(Locale.ROOT);
        for (String mode : MODES) {
            if (mode.startsWith(prefix)) {
                result.add(new TerminalSuggestion("kick " + mode, "kick " + mode, "Kick mode: " + mode));
            }
        }
        return result;
    }
}
