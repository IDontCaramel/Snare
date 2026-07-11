package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.caramel.snare.setting.BooleanSetting;

public final class SpectatorTpModule extends Module {
    public final BooleanSetting includeSpectators = addSetting(new BooleanSetting(
        "include_spectators", "Include Spectators", "Add spectator players to the spectator teleport menu.", false
    ));

    public SpectatorTpModule(Runnable saveRequest) {
        super("spectator_tp", "Spectator TP", "Controls spectator teleport menu behavior.", ModuleCategories.MISC, saveRequest);
    }

    public boolean includesSpectators() {
        return isEnabled() && includeSpectators.value();
    }
}