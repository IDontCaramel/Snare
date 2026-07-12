package net.caramel.snare.module.tweak;

import net.caramel.snare.event.EventBus;
import net.caramel.snare.event.EventPriority;
import net.caramel.snare.event.type.PortalScreenCheckEvent;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;

public final class PortalMenuModule extends Module {
    public PortalMenuModule(EventBus events, Runnable saveRequest) {
        super("portal_menu", "Portal Menu", "Allows opening menus while in a nether portal.", ModuleCategories.BEHAVIOR, saveRequest);
        events.subscribe(PortalScreenCheckEvent.class, EventPriority.NORMAL, this::isEnabled, event -> {
            event.blocksPortalClose(true);
        });
    }
}
