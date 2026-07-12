package net.caramel.snare.module.tweak;

import net.caramel.snare.event.EventBus;
import net.caramel.snare.event.EventPriority;
import net.caramel.snare.event.type.ClientDisconnectEvent;
import net.caramel.snare.event.type.ClientTickEvent;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.caramel.snare.setting.BooleanSetting;
import net.caramel.snare.setting.ModeSetting;
import net.caramel.snare.setting.NumberSetting;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class PotionSpoofModule extends Module {
    private final List<String> effectChoices;
    private final List<PotionSlot> slots = new ArrayList<>();
    private final Set<StatusEffect> managedEffects = new HashSet<>();

    private record PotionSlot(BooleanSetting enabled, ModeSetting effect, NumberSetting level) {}

    public PotionSpoofModule(EventBus events, Runnable saveRequest) {
        super("potion_spoof", "Potion Spoof", "Spoofs status effects client-side.", ModuleCategories.SPOOF, saveRequest);
        effectChoices = Registries.STATUS_EFFECT.getIds().stream()
            .sorted(Comparator.comparing(Identifier::toString))
            .map(Identifier::getPath)
            .collect(Collectors.toList());
        String defaultEffect = effectChoices.isEmpty() ? "" : effectChoices.get(0);
        for (int i = 1; i <= 10; i++) {
            String prefix = "slot_" + i;
            BooleanSetting enabled = addSetting(new BooleanSetting(
                prefix + "_enabled", "Slot " + i + " Enabled", "", false
            ));
            ModeSetting effect = addSetting(new ModeSetting(
                prefix + "_effect", "Slot " + i + " Effect", "", defaultEffect, effectChoices
            ));
            NumberSetting level = addSetting(new NumberSetting(
                prefix + "_level", "Slot " + i + " Level", "", 1.0, 1.0, 10.0, 1.0
            ));
            slots.add(new PotionSlot(enabled, effect, level));
        }

        events.subscribe(ClientTickEvent.class, EventPriority.NORMAL, this::isEnabled, event -> {
            var player = event.client().player;
            if (player == null) return;
            var desired = new java.util.LinkedHashMap<StatusEffect, Integer>();
            for (PotionSlot slot : slots) {
                if (!slot.enabled().value()) continue;
                String path = slot.effect().value();
                Registries.STATUS_EFFECT.getIds().stream()
                    .filter(id -> id.getPath().equals(path))
                    .findFirst()
                    .ifPresent(id -> {
                        StatusEffect effect = Registries.STATUS_EFFECT.get(id);
                        if (effect != null) {
                            int amplifier = slot.level().value().intValue() - 1;
                            desired.merge(effect, amplifier, Math::max);
                        }
                    });
            }
            var active = player.getActiveStatusEffects();
            for (var entry : desired.entrySet()) {
                StatusEffect effect = entry.getKey();
                int amplifier = entry.getValue();
                StatusEffectInstance existing = active.get(effect);
                if (existing == null || existing.getDuration() == 0) {
                    player.addStatusEffect(new StatusEffectInstance(effect, 0, amplifier));
                }
            }
            Set<StatusEffect> stillDesired = desired.keySet();
            for (StatusEffect effect : List.copyOf(managedEffects)) {
                if (!stillDesired.contains(effect)) {
                    StatusEffectInstance instance = active.get(effect);
                    if (instance != null && instance.getDuration() == 0) {
                        player.removeStatusEffect(effect);
                    }
                }
            }
            managedEffects.clear();
            managedEffects.addAll(stillDesired);
        });

        events.subscribe(ClientDisconnectEvent.class, EventPriority.NORMAL, this::isEnabled, event -> {
            cleanup(event.client().player);
        });
    }

    @Override
    protected void onDisable() {
        var client = net.minecraft.client.MinecraftClient.getInstance();
        cleanup(client.player);
    }

    private void cleanup(net.minecraft.client.network.ClientPlayerEntity player) {
        if (player != null) {
            for (StatusEffect effect : managedEffects) {
                StatusEffectInstance instance = player.getActiveStatusEffects().get(effect);
                if (instance != null && instance.getDuration() == 0) {
                    player.removeStatusEffect(effect);
                }
            }
        }
        managedEffects.clear();
    }
}
