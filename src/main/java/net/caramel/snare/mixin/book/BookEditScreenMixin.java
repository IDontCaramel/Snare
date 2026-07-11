package net.caramel.snare.mixin.book;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.caramel.snare.SnareClient;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.tweak.BookTweaksModule;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.util.SelectionManager;

@Mixin(BookEditScreen.class)
public abstract class BookEditScreenMixin {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SelectionManager;<init>(Ljava/util/function/Supplier;Ljava/util/function/Consumer;Ljava/util/function/Supplier;Ljava/util/function/Consumer;Ljava/util/function/Predicate;)V", ordinal = 0), index = 4)
    private Predicate<String> snare$bookCharacterCap(Predicate<String> original) {
        return SnareClient.modules().get(BookTweaksModule.class)
            .filter(Module::isEnabled)
            .map(module -> {
                int cap = module.characterCap.value().intValue();
                return (Predicate<String>) value -> value.length() <= cap;
            })
            .orElse(original);
    }
}