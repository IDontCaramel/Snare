package net.caramel.snare.mixin.interpolation;

import net.caramel.snare.SnareClient;
import net.caramel.snare.event.type.EntityInterpolationEvent;
import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BoatEntity.class)
public abstract class BoatEntityInterpolationMixin {
    @Shadow private int field_7708;

    @Inject(method = "updateTrackedPositionAndAngles(DDDFFIZ)V", at = @At("TAIL"))
    private void snare$boatInterpolation(double x, double y, double z, float yaw, float pitch, int steps, boolean interpolate, CallbackInfo ci) {
        EntityInterpolationEvent event = SnareClient.events().post(
            new EntityInterpolationEvent((BoatEntity) (Object) this, field_7708));
        field_7708 = event.steps();
    }
}
