package net.caramel.snare.mixin.movement;

import net.caramel.snare.SnareClient;
import net.caramel.snare.module.tweak.BoatTweaksModule;
import net.caramel.snare.module.tweak.SlipperyModule;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin extends Entity {
    @Shadow private float yawVelocity;
    @Shadow private boolean pressingLeft;
    @Shadow private boolean pressingRight;
    @Shadow private boolean pressingForward;

    protected BoatEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private ClientPlayerEntity snare$controller() {
        return getControllingPassenger() instanceof ClientPlayerEntity player ? player : null;
    }

    @Inject(method = "getNearbySlipperiness", at = @At("HEAD"), cancellable = true)
    private void snare$vehicleSlipperiness(CallbackInfoReturnable<Float> cir) {
        SlipperyModule module = SnareClient.modules().get(SlipperyModule.class).orElse(null);
        if (snare$controller() != null && module != null && module.isEnabled() && module.affectVehicles.value()) {
            cir.setReturnValue(module.slipperiness.value().floatValue());
        }
    }

    @Inject(method = "updatePaddles", at = @At("HEAD"))
    private void snare$boatSteering(CallbackInfo ci) {
        ClientPlayerEntity controller = snare$controller();
        BoatTweaksModule module = SnareClient.modules().get(BoatTweaksModule.class).orElse(null);
        if (controller == null || module == null || !module.isEnabled()) return;
        if (!pressingLeft && !pressingRight) yawVelocity = 0.0F;
        if (module.playerYaw.value()) setYaw(controller.getYaw());
    }

    @ModifyVariable(method = "setInputs", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private boolean snare$leftInput(boolean value) {
        BoatTweaksModule module = SnareClient.modules().get(BoatTweaksModule.class).orElse(null);
        return module != null && module.isEnabled() && module.playerYaw.value() ? false : value;
    }

    @ModifyVariable(method = "setInputs", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private boolean snare$rightInput(boolean value) {
        BoatTweaksModule module = SnareClient.modules().get(BoatTweaksModule.class).orElse(null);
        return module != null && module.isEnabled() && module.playerYaw.value() ? false : value;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void snare$boatJump(CallbackInfo ci) {
        BoatTweaksModule module = SnareClient.modules().get(BoatTweaksModule.class).orElse(null);
        if (getWorld().isClient && snare$controller() != null && pressingForward
                && module != null && module.isEnabled() && (isOnGround() || module.spiderBoat.value())) {
            Direction direction = getHorizontalFacing();
            BlockPos front = getBlockPos().offset(direction);
            BlockPos above = front.up();
            VoxelShape frontShape = getWorld().getBlockState(front).getCollisionShape(getWorld(), front);
            VoxelShape aboveShape = getWorld().getBlockState(above).getCollisionShape(getWorld(), above);
            double frontY = frontShape.isEmpty() ? 0.0 : frontShape.getMax(Direction.Axis.Y);
            double aboveY = aboveShape.isEmpty() ? 0.0 : aboveShape.getMax(Direction.Axis.Y);
            double rise = module.spiderBoat.value() ? 1.0
                : frontY < 1.0 ? frontY
                : frontY <= 1.0 && aboveY <= 0.5 ? frontY + aboveY
                : frontY <= 1.5 && aboveY == 0.0 ? frontY : 0.0;
            if (frontY > 0.0 && rise > 0.0 && getY() < front.getY() + rise) {
                setPosition(getX(), getY() + rise + 0.3, getZ());
            }
        }
    }
}
