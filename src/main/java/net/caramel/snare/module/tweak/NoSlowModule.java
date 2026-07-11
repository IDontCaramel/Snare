package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;

public final class NoSlowModule extends Module {
    public NoSlowModule(Runnable saveRequest) {
        super("no_slow", "NoSlow", "Removes item-use movement slowdown.", ModuleCategories.MOVEMENT, saveRequest);
    }
}
