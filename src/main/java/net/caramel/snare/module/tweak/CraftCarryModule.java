package net.caramel.snare.module.tweak;

import net.caramel.snare.event.EventBus;
import net.caramel.snare.event.EventPriority;
import net.caramel.snare.event.type.PacketSendEvent;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public final class CraftCarryModule extends Module {
    public CraftCarryModule(EventBus events, Runnable saveRequest) {
        super("craft_carry", "Craft Carry", "Allows keeping items in the 2x2 crafting grid after closing the inventory.", ModuleCategories.BEHAVIOR, saveRequest);
        events.subscribe(PacketSendEvent.class, EventPriority.NORMAL, this::isEnabled, event -> {
            if (event.packet() instanceof CloseHandledScreenC2SPacket close) {
                if (close.getSyncId() == 0) {
                    event.cancel();
                }
            }
        });
    }
}
