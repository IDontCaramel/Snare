package net.caramel.snare.command;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.caramel.snare.SnareClient;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.text.Text;

public final class SpectatorTeleportCommand {
    private SpectatorTeleportCommand() {
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
            ClientCommandManager.literal("spectatortp")
                .then(ClientCommandManager.argument("player", StringArgumentType.word())
                    .suggests(SpectatorTeleportCommand::suggestPlayers)
                    .executes(SpectatorTeleportCommand::execute))
        ));
    }

    private static CompletableFuture<Suggestions> suggestPlayers(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
        FabricClientCommandSource source = context.getSource();
        ClientPlayNetworkHandler network = source.getClient().getNetworkHandler();
        if (network == null) {
            return builder.buildFuture();
        }
        Stream<String> names = network.getListedPlayerListEntries().stream()
            .map(entry -> entry.getProfile().getName());
        return CommandSource.suggestMatching(names, builder);
    }

    private static int execute(CommandContext<FabricClientCommandSource> context) {
        String requested = StringArgumentType.getString(context, "player");
        FabricClientCommandSource source = context.getSource();
        ClientPlayNetworkHandler network = source.getClient().getNetworkHandler();
        if (network == null) {
            source.sendError(Text.literal("Not connected to a server."));
            return 0;
        }
        PlayerListEntry target = network.getListedPlayerListEntries().stream()
            .filter(entry -> entry.getProfile().getName().equalsIgnoreCase(requested))
            .findFirst()
            .orElse(null);
        if (target == null) {
            source.sendError(Text.literal("Player not found: " + requested));
            return 0;
        }
        network.sendPacket(new SpectatorTeleportC2SPacket(target.getProfile().getId()));
        return 1;
    }
}