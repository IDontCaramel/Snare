package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;

public final class NoJumpDelayModule extends Module {
    public NoJumpDelayModule(Runnable saveRequest) {
        super("no_jump_delay", "No Jump Delay", "Removes the local-player jump cooldown.", ModuleCategories.REMOVAL, saveRequest);
    }
}
