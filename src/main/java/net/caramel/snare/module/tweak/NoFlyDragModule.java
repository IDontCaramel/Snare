package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.caramel.snare.setting.NumberSetting;

public final class NoFlyDragModule extends Module {
    public final NumberSetting drag = addSetting(new NumberSetting(
        "drag", "Drag", "Creative-flight momentum reduction.", 0.0, 0.0, 1.0, 0.01
    ));

    public NoFlyDragModule(Runnable saveRequest) {
        super("no_fly_drag", "No Fly Drag", "Controls creative-flight drag.", ModuleCategories.MOVEMENT, saveRequest);
    }
}