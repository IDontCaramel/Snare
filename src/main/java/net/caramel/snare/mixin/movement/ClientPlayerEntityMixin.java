package net.caramel.snare.mixin.movement;

import net.caramel.snare.SnareClient;
import net.caramel.snare.module.tweak.HorseTweaksModule;
import net.caramel.snare.module.tweak.NoSlowModule;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.JumpingMount;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @ModifyConstant(method = "tickMovement", constant = @Constant(floatValue = 0.2F))
    private float snare$itemUseSlowdown(float multiplier) {
        NoSlowModule module = SnareClient.modules().get(NoSlowModule.class).orElse(null);
        return module != null && module.isEnabled() ? 1.0F : multiplier;
    }

    @Redirect(
        method = {"tickMovement", "startRidingJump"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getMountJumpStrength()F")
    )
    private float snare$fullMountJump(ClientPlayerEntity player) {
        HorseTweaksModule module = SnareClient.modules().get(HorseTweaksModule.class).orElse(null);
        return module != null && module.isEnabled() ? 1.0F : player.getMountJumpStrength();
    }

    @Redirect(
        method = "tickMovement",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/JumpingMount;getJumpCooldown()I")
    )
    private int snare$mountJumpCooldown(JumpingMount mount) {
        HorseTweaksModule module = SnareClient.modules().get(HorseTweaksModule.class).orElse(null);
        return module != null && module.isEnabled() ? 0 : mount.getJumpCooldown();
    }
}
