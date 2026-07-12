package net.caramel.snare.mixin.interpolation;

import net.caramel.snare.SnareClient;
import net.caramel.snare.event.type.EntityInterpolationEvent;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityInterpolationMixin {
    @Shadow private int clientInterpolationSteps;

    @Inject(method = "updateTrackedPositionAndAngles(DDDFFIZ)V", at = @At("TAIL"))
    private void snare$minecartInterpolation(double x, double y, double z, float yaw, float pitch, int steps, boolean interpolate, CallbackInfo ci) {
        EntityInterpolationEvent event = SnareClient.events().post(
            new EntityInterpolationEvent((AbstractMinecartEntity) (Object) this, clientInterpolationSteps));
        clientInterpolationSteps = event.steps();
    }
}
