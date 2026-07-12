package net.caramel.snare.mixin.movement;

import net.caramel.snare.SnareClient;
import net.caramel.snare.event.type.PlayerJumpVelocityEvent;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class PlayerJumpMixin {
    @Shadow protected abstract float getJumpVelocity();

    @Redirect(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getJumpVelocity()F"))
    private float snare$jumpVelocity(LivingEntity self) {
        float original = getJumpVelocity();
        PlayerJumpVelocityEvent event = SnareClient.events().post(new PlayerJumpVelocityEvent(self, original));
        return event.velocity();
    }
}
