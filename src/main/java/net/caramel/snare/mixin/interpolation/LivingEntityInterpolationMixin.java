package net.caramel.snare.mixin.interpolation;

import net.caramel.snare.SnareClient;
import net.caramel.snare.event.type.EntityInterpolationEvent;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityInterpolationMixin {
    @Shadow protected int bodyTrackingIncrements;
    @Shadow protected int headTrackingIncrements;

    @Inject(method = "updateTrackedPositionAndAngles(DDDFFIZ)V", at = @At("TAIL"))
    private void snare$bodyInterpolation(double x, double y, double z, float yaw, float pitch, int steps, boolean interpolate, CallbackInfo ci) {
        EntityInterpolationEvent event = SnareClient.events().post(
            new EntityInterpolationEvent((LivingEntity) (Object) this, bodyTrackingIncrements));
        bodyTrackingIncrements = event.steps();
    }

    @Inject(method = "updateTrackedHeadRotation(FI)V", at = @At("TAIL"))
    private void snare$headInterpolation(float yaw, int steps, CallbackInfo ci) {
        EntityInterpolationEvent event = SnareClient.events().post(
            new EntityInterpolationEvent((LivingEntity) (Object) this, headTrackingIncrements));
        headTrackingIncrements = event.steps();
    }
}
