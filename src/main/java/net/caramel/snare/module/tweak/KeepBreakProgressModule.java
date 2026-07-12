package net.caramel.snare.module.tweak;

import net.caramel.snare.event.EventBus;
import net.caramel.snare.event.EventPriority;
import net.caramel.snare.event.type.CancelBlockBreakingEvent;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;

public final class KeepBreakProgressModule extends Module {
    public KeepBreakProgressModule(EventBus events, Runnable saveRequest) {
        super("keep_break_progress", "Keep Break Progress", "Prevents block breaking progress from resetting when switching slots.", ModuleCategories.BEHAVIOR, saveRequest);
        events.subscribe(CancelBlockBreakingEvent.class, EventPriority.NORMAL, this::isEnabled, event -> {
            event.cancel();
        });
    }
}
