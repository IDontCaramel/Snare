package net.caramel.snare.mixin.spectator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.gui.hud.spectator.TeleportToSpecificPlayerSpectatorCommand;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(TeleportToSpecificPlayerSpectatorCommand.class)
public class TeleportToSpecificPlayerSpectatorCommandMixin implements SpectatorMenuEntry {
    @Shadow
    @org.spongepowered.asm.mixin.Final
    private GameProfile gameProfile;

    private boolean snare$spectator = false;

    @Override
    public void snare$setSpectator(boolean spectator) {
        this.snare$spectator = spectator;
    }

    @Inject(method = "getName()Lnet/minecraft/text/Text;", at = @At("HEAD"), cancellable = true)
    private void snare$styleSpectatorName(CallbackInfoReturnable<Text> cir) {
        if (this.snare$spectator) {
            cir.setReturnValue(Text.literal(this.gameProfile.getName()).formatted(Formatting.GRAY, Formatting.ITALIC));
        }
    }
}