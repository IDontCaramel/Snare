package net.caramel.snare.mixin.movement;

import net.caramel.snare.SnareClient;
import net.caramel.snare.module.tweak.GravityTweaksModule;
import net.caramel.snare.module.tweak.NoFlyDragModule;
import net.caramel.snare.module.tweak.NoJumpDelayModule;
import net.caramel.snare.module.tweak.SlipperyModule;
import net.minecraft.block.Block;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow private int jumpingCooldown;

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V", ordinal = 3))
    private void snare$creativeFlightDrag(LivingEntity self, double x, double y, double z) {
        NoFlyDragModule module = SnareClient.modules().get(NoFlyDragModule.class).orElse(null);
        if (module == null || !module.isEnabled() || !(self instanceof ClientPlayerEntity player)
                || !player.getAbilities().flying || player.isOnGround()) {
            self.setVelocity(x, y, z);
            return;
        }
        double horizontal = 1.0 - module.drag.value();
        net.minecraft.util.math.Vec3d velocity = self.getVelocity();
        self.setVelocity(velocity.x * horizontal, y, velocity.z * horizontal);
    }

    @Redirect(method = "travel", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/block/Block;getSlipperiness()F"
    ))
    private float snare$slipperiness(Block block) {
        LivingEntity self = (LivingEntity) (Object) this;
        SlipperyModule module = SnareClient.modules().get(SlipperyModule.class).orElse(null);
        boolean localPlayer = self instanceof ClientPlayerEntity;
        boolean controlledVehicle = module != null && module.affectVehicles.value()
            && self.getControllingPassenger() instanceof ClientPlayerEntity;
        return module != null && module.isEnabled() && (localPlayer || controlledVehicle)
            ? module.slipperiness.value().floatValue()
            : block.getSlipperiness();
    }

    @ModifyVariable(method = "travel", at = @At(value = "STORE", ordinal = 0), index = 2)
    private double snare$gravity(double gravity) {
        if (!((Object) this instanceof ClientPlayerEntity)) return gravity;
        GravityTweaksModule module = SnareClient.modules().get(GravityTweaksModule.class).orElse(null);
        return module != null && module.isEnabled() ? module.gravity.value() : gravity;
    }

    @Redirect(method = "tickMovement", at = @At(
        value = "FIELD",
        target = "Lnet/minecraft/entity/LivingEntity;jumpingCooldown:I",
        opcode = Opcodes.PUTFIELD
    ))
    private void snare$jumpDelay(LivingEntity instance, int value) {
        NoJumpDelayModule module = SnareClient.modules().get(NoJumpDelayModule.class).orElse(null);
        this.jumpingCooldown = module != null && module.isEnabled()
            && (Object) this instanceof ClientPlayerEntity ? 0 : value;
    }
}
