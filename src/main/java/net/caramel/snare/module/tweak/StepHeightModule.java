package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.caramel.snare.setting.NumberSetting;

public final class StepHeightModule extends Module {
    public final NumberSetting height = addSetting(new NumberSetting(
        "height", "Step Height", "Maximum automatic step height.", 0.6, 0.0, 1.5, 0.01
    ));

    public StepHeightModule(Runnable saveRequest) {
        super("step_height", "Step Height", "Controls local-player step height.", ModuleCategories.MOVEMENT, saveRequest);
    }
}
