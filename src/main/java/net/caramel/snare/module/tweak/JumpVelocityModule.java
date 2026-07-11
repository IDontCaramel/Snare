package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.caramel.snare.setting.NumberSetting;

public final class JumpVelocityModule extends Module {
    public final NumberSetting velocity = addSetting(new NumberSetting(
        "velocity", "Jump Velocity", "Overrides the block jump factor.", 1.15, 0.5, 5.0, 0.01
    ));

    public JumpVelocityModule(Runnable saveRequest) {
        super("jump_velocity", "Jump Velocity", "Controls local-player jump velocity.", ModuleCategories.MOVEMENT, saveRequest);
    }
}
