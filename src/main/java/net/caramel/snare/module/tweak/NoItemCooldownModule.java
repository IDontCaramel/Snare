package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;

public final class NoItemCooldownModule extends Module {
    public NoItemCooldownModule(Runnable saveRequest) {
        super("no_item_cooldown", "No Item Cooldown", "Ignores newly applied client item cooldowns.", ModuleCategories.BEHAVIOR, saveRequest);
    }
}