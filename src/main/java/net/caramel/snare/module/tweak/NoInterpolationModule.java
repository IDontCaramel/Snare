package net.caramel.snare.module.tweak;

import net.caramel.snare.event.EventBus;
import net.caramel.snare.event.EventPriority;
import net.caramel.snare.event.type.EntityInterpolationEvent;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.caramel.snare.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;

public final class NoInterpolationModule extends Module {
    public final NumberSetting reduceBy;

    public NoInterpolationModule(EventBus events, Runnable saveRequest) {
        super("no_interpolation", "No Interpolation", "Reduces entity interpolation steps.", ModuleCategories.REMOVAL, saveRequest);
        this.reduceBy = addSetting(new NumberSetting("reduce_by", "Reduce By", "", 3.0, 1.0, 3.0, 1.0));
        events.subscribe(EntityInterpolationEvent.class, EventPriority.NORMAL, this::isEnabled, event -> {
            if (event.entity() != MinecraftClient.getInstance().player) {
                event.steps(Math.max(0, event.steps() - reduceBy.value().intValue()));
            }
        });
    }
}
