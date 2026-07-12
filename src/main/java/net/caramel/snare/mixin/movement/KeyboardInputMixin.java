package net.caramel.snare.mixin.movement;

import net.caramel.snare.SnareClient;
import net.caramel.snare.event.type.MovementInputEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin {
    @Inject(method = "tick(ZF)V", at = @At("TAIL"))
    private void snare$movementInput(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
        Input input = (Input) (Object) this;
        boolean screenOpen = MinecraftClient.getInstance().currentScreen != null;
        MovementInputEvent event = SnareClient.events().post(new MovementInputEvent(
            input.pressingForward, input.pressingBack, input.pressingLeft, input.pressingRight, screenOpen
        ));
        input.pressingForward = event.forward();
        input.pressingBack = event.back();
        input.pressingLeft = event.left();
        input.pressingRight = event.right();

        input.movementForward = getMultiplier(input.pressingForward, input.pressingBack);
        input.movementSideways = getMultiplier(input.pressingLeft, input.pressingRight);
        if (slowDown) {
            input.movementForward *= slowDownFactor;
            input.movementSideways *= slowDownFactor;
        }
    }

    private static float getMultiplier(boolean positive, boolean negative) {
        if (positive == negative) return 0;
        return positive ? 1 : -1;
    }
}
