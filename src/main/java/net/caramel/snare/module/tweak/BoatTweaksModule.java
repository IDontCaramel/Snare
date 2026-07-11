package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.caramel.snare.setting.BooleanSetting;

public final class BoatTweaksModule extends Module {
    public final BooleanSetting spiderBoat = addSetting(new BooleanSetting(
        "spider_boat", "Spider Boat", "Lets a forward-moving boat climb full blocks.", false
    ));
    public final BooleanSetting playerYaw = addSetting(new BooleanSetting(
        "player_yaw", "Player Yaw", "Steers the boat using the controlling player's yaw.", false
    ));

    public BoatTweaksModule(Runnable saveRequest) {
        super("boat_tweaks", "Boat Tweaks", "Adds obstacle jumping and tighter boat steering.", ModuleCategories.MOVEMENT, saveRequest);
    }
}
