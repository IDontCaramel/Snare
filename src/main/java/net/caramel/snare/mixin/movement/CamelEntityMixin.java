package net.caramel.snare.mixin.movement;

import net.caramel.snare.SnareClient;
import net.caramel.snare.module.tweak.HorseTweaksModule;
import net.minecraft.entity.passive.CamelEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CamelEntity.class)
public abstract class CamelEntityMixin {
    @Redirect(
        method = "setJumpStrength",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/passive/CamelEntity;dashCooldown:I", opcode = Opcodes.GETFIELD)
    )
    private int snare$readDashCooldown(CamelEntity camel) {
        HorseTweaksModule module = SnareClient.modules().get(HorseTweaksModule.class).orElse(null);
        return module != null && module.isEnabled() ? 0 : camel.getJumpCooldown();
    }

    @ModifyConstant(method = "jump", constant = @Constant(intValue = 55))
    private int snare$writeDashCooldown(int cooldown) {
        HorseTweaksModule module = SnareClient.modules().get(HorseTweaksModule.class).orElse(null);
        return module != null && module.isEnabled() ? 0 : cooldown;
    }

    @Inject(method = "getJumpCooldown", at = @At("HEAD"), cancellable = true)
    private void snare$getDashCooldown(CallbackInfoReturnable<Integer> cir) {
        HorseTweaksModule module = SnareClient.modules().get(HorseTweaksModule.class).orElse(null);
        if (module != null && module.isEnabled()) cir.setReturnValue(0);
    }
}
