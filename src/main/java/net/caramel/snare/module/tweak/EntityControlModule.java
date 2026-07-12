package net.caramel.snare.module.tweak;

import net.caramel.snare.event.EventBus;
import net.caramel.snare.event.EventPriority;
import net.caramel.snare.event.type.MountJumpStrengthEvent;
import net.caramel.snare.event.type.SaddleCheckEvent;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.caramel.snare.setting.BooleanSetting;

public final class EntityControlModule extends Module {
    public final BooleanSetting saddled;
    public final BooleanSetting fullJump;

    public EntityControlModule(EventBus events, Runnable saveRequest) {
        super("entity_control", "Entity Control", "Controls mount saddling and jump strength.", ModuleCategories.MOVEMENT, saveRequest);
        this.saddled = addSetting(new BooleanSetting("saddled", "Saddled", "", true));
        this.fullJump = addSetting(new BooleanSetting("full_jump_strength", "Full Jump Strength", "", true));
        events.subscribe(SaddleCheckEvent.class, EventPriority.NORMAL, this::isEnabled, event -> {
            if (saddled.value()) event.saddled(true);
        });
        events.subscribe(MountJumpStrengthEvent.class, EventPriority.NORMAL, this::isEnabled, event -> {
            if (fullJump.value()) event.strength(1.0F);
        });
    }
}
