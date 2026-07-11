package net.caramel.snare.mixin.inventory;

import net.caramel.snare.SnareClient;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.tweak.NoArmorRestrictionModule;
import net.caramel.snare.module.tweak.NoBindingCurseModule;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.screen.PlayerScreenHandler$1")
public abstract class PlayerArmorSlotMixin extends Slot {
    public PlayerArmorSlotMixin(PlayerInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    private void snare$allowAnyArmor(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (SnareClient.modules().get(NoArmorRestrictionModule.class).filter(Module::isEnabled).isPresent()) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "canTakeItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;hasBindingCurse(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean snare$ignoreBindingCurse(ItemStack stack) {
        if (SnareClient.modules().get(NoBindingCurseModule.class).filter(Module::isEnabled).isPresent()) {
            return false;
        }
        return EnchantmentHelper.hasBindingCurse(stack);
    }
}