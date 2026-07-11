package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;

public final class FogControlModule extends Module {
    public FogControlModule(Runnable saveRequest) {
        super("fog_control", "Fog Control", "Suppresses environment-specific fog.", ModuleCategories.RENDER, saveRequest);
    }
}