package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;

public final class NoSignLimitModule extends Module {
    public NoSignLimitModule(Runnable saveRequest) {
        super("no_sign_limit", "No Sign Limit", "Removes client sign text width limits.", ModuleCategories.REMOVAL, saveRequest);
    }
}