package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;

public final class NoArmorRestrictionModule extends Module {
    public NoArmorRestrictionModule(Runnable saveRequest) {
        super("no_armor_restriction", "No Armor Restriction", "Allows any item in client armor slots.", ModuleCategories.REMOVAL, saveRequest);
    }
}