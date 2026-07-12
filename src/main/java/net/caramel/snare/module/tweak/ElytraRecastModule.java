package net.caramel.snare.module.tweak;

import net.caramel.snare.SnareClient;
import net.caramel.snare.event.EventBus;
import net.caramel.snare.event.EventPriority;
import net.caramel.snare.event.type.ClientDisconnectEvent;
import net.caramel.snare.event.type.ClientTickEvent;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public final class ElytraRecastModule extends Module {
    private ClientPlayerEntity trackedPlayer;
    private boolean wasFallFlying;

    public ElytraRecastModule(EventBus events, Runnable saveRequest) {
        super("elytra_recast", "Elytra Recast", "Automatically recasts elytra flight when jump is pressed.", ModuleCategories.MOVEMENT, saveRequest);
        events.subscribe(ClientTickEvent.class, EventPriority.NORMAL, this::isEnabled, event -> {
            ClientPlayerEntity player = event.client().player;
            if (player == null) return;
            if (player != trackedPlayer) {
                trackedPlayer = player;
                wasFallFlying = false;
            }
            if (wasFallFlying && !player.isFallFlying()) {
                if (recastConditions(player)) {
                    player.startFallFlying();
                    player.networkHandler.sendPacket(new ClientCommandC2SPacket(
                        player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                }
            }
            wasFallFlying = player.isFallFlying();
        });
        events.subscribe(ClientDisconnectEvent.class, EventPriority.NORMAL, this::isEnabled, event -> {
            trackedPlayer = null;
            wasFallFlying = false;
        });
    }

    private boolean recastConditions(ClientPlayerEntity player) {
        return MinecraftClient.getInstance().options.jumpKey.isPressed()
            && !player.getAbilities().flying
            && !player.hasVehicle()
            && !player.isClimbing()
            && !player.isTouchingWater()
            && !player.hasStatusEffect(StatusEffects.LEVITATION)
            && hasUsableElytra(player)
            && !player.isOnGround();
    }

    private boolean hasUsableElytra(ClientPlayerEntity player) {
        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
        return chest.isOf(Items.ELYTRA) && ElytraItem.isUsable(chest);
    }

    @Override
    protected void onEnable() {
        SnareClient.modules().get(CreativeElytraFlightModule.class).ifPresent(m -> m.setEnabled(false));
    }

    @Override
    protected void onDisable() {
        trackedPlayer = null;
        wasFallFlying = false;
    }
}
