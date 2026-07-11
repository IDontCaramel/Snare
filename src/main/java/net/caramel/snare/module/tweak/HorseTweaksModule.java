package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;

public final class HorseTweaksModule extends Module {
    public HorseTweaksModule(Runnable saveRequest) {
        super("horse_tweaks", "Horse Tweaks", "Removes mounted jump charging and cooldowns.", ModuleCategories.MOVEMENT, saveRequest);
    }
}
