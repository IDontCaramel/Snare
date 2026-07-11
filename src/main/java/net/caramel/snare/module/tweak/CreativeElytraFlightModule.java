package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.entity.effect.StatusEffects;

public final class CreativeElytraFlightModule extends Module {
    private ClientPlayerEntity managedPlayer;
    private boolean previousAllowFlying;
    private boolean managedFlight;
    private boolean resumingGlide;

    public CreativeElytraFlightModule(Runnable saveRequest) {
        super("creative_elytra_flight", "Creative Elytra Flight", "Switches an elytra glide into creative-style flight.", ModuleCategories.MOVEMENT, saveRequest);
    }

    public void begin(ClientPlayerEntity player) {
        if (resumingGlide || managedFlight) return;
        managedPlayer = player;
        previousAllowFlying = player.getAbilities().allowFlying;
        player.getAbilities().allowFlying = true;
        player.getAbilities().flying = true;
        player.stopFallFlying();
        managedFlight = true;
    }

    @Override
    public void onClientTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (managedFlight && player != managedPlayer) restore(managedPlayer);
        if (player == null || !managedFlight || player != managedPlayer) return;

        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
        boolean validGlide = chest.isOf(Items.ELYTRA) && ElytraItem.isUsable(chest);
        if (player.isOnGround() || player.hasVehicle() || player.isTouchingWater()
                || player.hasStatusEffect(StatusEffects.LEVITATION) || !validGlide) {
            restore(player);
        } else if (!player.getAbilities().flying) {
            player.getAbilities().allowFlying = previousAllowFlying;
            managedFlight = false;
            managedPlayer = null;
            resumingGlide = true;
            player.startFallFlying();
            resumingGlide = false;
        } else {
            player.stopFallFlying();
        }
    }

    @Override
    protected void onDisable() {
        reset();
    }

    public void reset() {
        restore(managedPlayer);
    }

    private void restore(ClientPlayerEntity player) {
        if (player != null && managedFlight) {
            player.getAbilities().allowFlying = previousAllowFlying;
            player.getAbilities().flying = false;
        }
        managedPlayer = null;
        managedFlight = false;
        resumingGlide = false;
    }
}
