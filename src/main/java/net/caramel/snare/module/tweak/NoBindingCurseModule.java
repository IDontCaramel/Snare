package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;

public final class NoBindingCurseModule extends Module {
    public NoBindingCurseModule(Runnable saveRequest) {
        super("no_binding_curse", "No Binding Curse", "Allows client attempts to remove binding-cursed armor.", ModuleCategories.REMOVAL, saveRequest);
    }
}