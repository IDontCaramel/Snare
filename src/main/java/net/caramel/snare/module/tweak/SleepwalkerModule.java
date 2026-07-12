package net.caramel.snare.module.tweak;

import net.caramel.snare.event.EventBus;
import net.caramel.snare.event.EventPriority;
import net.caramel.snare.event.type.ClientTickEvent;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.minecraft.entity.EntityPose;

public final class SleepwalkerModule extends Module {
    public SleepwalkerModule(EventBus events, Runnable saveRequest) {
        super("sleepwalker", "Sleepwalker", "Allows movement while sleeping.", ModuleCategories.BEHAVIOR, saveRequest);
        events.subscribe(ClientTickEvent.class, EventPriority.NORMAL, this::isEnabled, event -> {
            var player = event.client().player;
            if (player != null && player.isSleeping()) {
                player.setPose(EntityPose.STANDING);
                player.clearSleepingPosition();
                event.client().setScreen(null);
            }
        });
    }
}
