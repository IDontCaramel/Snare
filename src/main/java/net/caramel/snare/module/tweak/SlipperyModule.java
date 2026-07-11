package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.caramel.snare.setting.BooleanSetting;
import net.caramel.snare.setting.NumberSetting;

public final class SlipperyModule extends Module {
    public final NumberSetting slipperiness = addSetting(new NumberSetting(
        "slipperiness", "Slipperiness", "Overrides ground slipperiness.", 0.98, 0.6, 1.15, 0.01
    ));
    public final BooleanSetting affectVehicles = addSetting(new BooleanSetting(
        "affect_vehicles", "Affect Vehicles", "Applies slipperiness to the controlled vehicle.", false
    ));

    public SlipperyModule(Runnable saveRequest) {
        super("slippery", "Slippery", "Controls local-player ground slipperiness.", ModuleCategories.MOVEMENT, saveRequest);
    }
}
