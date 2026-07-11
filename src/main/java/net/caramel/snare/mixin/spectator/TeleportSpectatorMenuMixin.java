package net.caramel.snare.mixin.spectator;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.caramel.snare.SnareClient;
import net.caramel.snare.module.tweak.SpectatorTpModule;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.TeleportSpectatorMenu;
import net.minecraft.client.gui.hud.spectator.TeleportToSpecificPlayerSpectatorCommand;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.world.GameMode;

@Mixin(TeleportSpectatorMenu.class)
public class TeleportSpectatorMenuMixin {
    @Shadow
    @Final
    @Mutable
    private List<SpectatorMenuCommand> elements;

    @Inject(method = "<init>(Ljava/util/Collection;)V", at = @At("TAIL"))
    private void snare$rebuildWithSpectators(Collection<PlayerListEntry> entries, CallbackInfo ci) {
        SpectatorTpModule module = SnareClient.modules().get(SpectatorTpModule.class).orElse(null);
        if (module == null || !module.includesSpectators()) {
            return;
        }
        List<SpectatorMenuCommand> rebuilt = entries.stream()
            .sorted(Comparator.comparing(entry -> entry.getProfile().getId()))
            .map(entry -> {
                TeleportToSpecificPlayerSpectatorCommand command = new TeleportToSpecificPlayerSpectatorCommand(entry.getProfile());
                if (entry.getGameMode() == GameMode.SPECTATOR) {
                    ((SpectatorMenuEntry) command).snare$setSpectator(true);
                }
                return (SpectatorMenuCommand) command;
            })
            .toList();
        this.elements = rebuilt;
    }
}