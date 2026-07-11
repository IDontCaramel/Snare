package net.caramel.snare.mixin.movement;

import net.caramel.snare.SnareClient;
import net.caramel.snare.module.tweak.NoFlyDragModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(DDD)V", ordinal = 3))
    private void snare$creativeFlightDrag(double x, double y, double z) {
        NoFlyDragModule module = SnareClient.modules().get(NoFlyDragModule.class).orElse(null);
        Entity self = (Entity) (Object) this;
        if (module == null || !module.isEnabled() || !(self instanceof ClientPlayerEntity player)
                || !player.getAbilities().flying || player.isOnGround()) {
            self.setVelocity(x, y, z);
            return;
        }
        double horizontal = 1.0 - module.drag.value();
        net.minecraft.util.math.Vec3d velocity = self.getVelocity();
        self.setVelocity(velocity.x * horizontal, y, velocity.z * horizontal);
    }
}