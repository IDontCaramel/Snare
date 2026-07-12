package net.caramel.snare.event.type;

import net.minecraft.client.MinecraftClient;

public record ClientDisconnectEvent(MinecraftClient client) {}
