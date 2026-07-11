package net.caramel.snare.module.tweak;

import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.caramel.snare.setting.NumberSetting;

public final class BookTweaksModule extends Module {
    public final NumberSetting characterCap = addSetting(new NumberSetting(
        "character_cap", "Character Cap", "Maximum editable characters per page.", 1024, 100, 1024, 1
    ));

    public BookTweaksModule(Runnable saveRequest) {
        super("book_tweaks", "Book Tweaks", "Controls client book editor limits.", ModuleCategories.REMOVAL, saveRequest);
    }
}