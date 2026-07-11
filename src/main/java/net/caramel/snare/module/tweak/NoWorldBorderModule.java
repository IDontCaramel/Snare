package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;

public final class NoWorldBorderModule extends Module {
    public NoWorldBorderModule(Runnable saveRequest) {
        super("no_world_border", "No World Border", "Suppresses the client world border.", ModuleCategories.RENDER, saveRequest);
    }
}