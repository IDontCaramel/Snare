package net.caramel.snare.module;
import java.util.*;
import net.caramel.snare.setting.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public abstract class Module {
    private static final Logger LOGGER = LoggerFactory.getLogger("snare/modules");
    private final String id, name, description; private final ModuleCategory category; private final Runnable saveRequest;
    private final List<Setting<?>> settings = new ArrayList<>(); private boolean enabled;
    protected Module(String id, String name, String description, ModuleCategory category, Runnable saveRequest) {
        if (id == null || id.isBlank() || name == null || name.isBlank()) throw new IllegalArgumentException("module fields must not be blank");
        this.id=id; this.name=name; this.description=Objects.requireNonNull(description); this.category=Objects.requireNonNull(category); this.saveRequest=Objects.requireNonNull(saveRequest);
    }
    protected final <T extends Setting<?>> T addSetting(T setting) {
        if (settings.stream().anyMatch(existing -> existing.id().equals(setting.id()))) throw new IllegalArgumentException("duplicate setting id " + setting.id());
        setting.setChangeListener(saveRequest); settings.add(setting); return setting;
    }
    public final void setEnabled(boolean next) {
        if (enabled == next) return; enabled = next;
        try { if (enabled) onEnable(); else onDisable(); } catch (RuntimeException exception) { LOGGER.error("Module {} lifecycle failed", id, exception); }
        saveRequest.run();
    }
    public final void toggle() { setEnabled(!enabled); }
    protected void onEnable() {} protected void onDisable() {} public void onClientTick(net.minecraft.client.MinecraftClient client) {}
    public final String id(){return id;} public final String name(){return name;} public final String description(){return description;}
    public final ModuleCategory category(){return category;} public final boolean isEnabled(){return enabled;}
    public final List<Setting<?>> settings(){return List.copyOf(settings);}
    public final Optional<KeybindSetting> keybind(){return settings.stream().filter(KeybindSetting.class::isInstance).map(KeybindSetting.class::cast).findFirst();}
}