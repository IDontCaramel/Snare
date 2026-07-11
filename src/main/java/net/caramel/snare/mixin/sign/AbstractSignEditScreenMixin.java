package net.caramel.snare.mixin.sign;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.caramel.snare.SnareClient;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.tweak.NoSignLimitModule;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.util.SelectionManager;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin {
    @Shadow
    @Final
    private SignBlockEntity blockEntity;

    @Shadow
    @Final
    private String[] messages;

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SelectionManager;<init>(Ljava/util/function/Supplier;Ljava/util/function/Consumer;Ljava/util/function/Supplier;Ljava/util/function/Consumer;Ljava/util/function/Predicate;)V"), index = 4)
    private Predicate<String> snare$allowLongSignText(Predicate<String> original) {
        return SnareClient.modules().get(NoSignLimitModule.class)
            .filter(Module::isEnabled)
            .map(module -> (Predicate<String>) value -> true)
            .orElse(original);
    }

    @Inject(method = "renderSignText", at = @At("TAIL"))
    private void snare$drawSignOverflowWarnings(DrawContext context, CallbackInfo ci) {
        boolean enabled = SnareClient.modules().get(NoSignLimitModule.class)
            .filter(Module::isEnabled)
            .isPresent();
        if (!enabled) {
            return;
        }
        net.minecraft.client.font.TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        for (int line = 0; line < this.messages.length; line++) {
            String message = this.messages[line];
            if (textRenderer.getWidth(message) > this.blockEntity.getMaxTextWidth()) {
                int lineHeight = this.blockEntity.getTextLineHeight();
                int centerOffset = 4 * lineHeight / 2;
                int width = textRenderer.getWidth(message);
                context.drawText(textRenderer, "!", -width / 2 - 10, line * lineHeight - centerOffset, 0xFFFF5555, false);
            }
        }
    }
}
