package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.caramel.snare.setting.NumberSetting;

public final class GravityTweaksModule extends Module {
    public final NumberSetting gravity = addSetting(new NumberSetting(
        "gravity", "Gravity", "Overrides local-player gravity.", 0.08, -0.5, 0.5, 0.01
    ));

    public GravityTweaksModule(Runnable saveRequest) {
        super("gravity_tweaks", "Gravity Tweaks", "Controls local-player gravity.", ModuleCategories.BEHAVIOR, saveRequest);
    }
}
