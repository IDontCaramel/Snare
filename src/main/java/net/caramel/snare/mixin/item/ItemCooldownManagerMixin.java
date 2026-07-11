package net.caramel.snare.mixin.item;

import net.caramel.snare.SnareClient;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.tweak.NoItemCooldownModule;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemCooldownManager.class)
public class ItemCooldownManagerMixin {
    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    private void snare$suppressCooldown(Item item, int cooldown, CallbackInfo ci) {
        if (SnareClient.modules().get(NoItemCooldownModule.class).filter(Module::isEnabled).isPresent()) {
            ci.cancel();
        }
    }
}