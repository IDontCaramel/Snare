package net.caramel.snare.mixin.movement;

import net.caramel.snare.SnareClient;
import net.caramel.snare.event.type.SaddleCheckEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.StriderEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractHorseEntity.class, PigEntity.class, StriderEntity.class})
public abstract class SaddleableEntityMixin {
    @Inject(method = "isSaddled()Z", at = @At("RETURN"), cancellable = true)
    private void snare$saddleCheck(CallbackInfoReturnable<Boolean> cir) {
        SaddleCheckEvent event = SnareClient.events().post(
            new SaddleCheckEvent((Entity) (Object) this, cir.getReturnValueZ()));
        cir.setReturnValue(event.saddled());
    }
}
