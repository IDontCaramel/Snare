package net.caramel.snare.module;
import java.util.*;
import java.util.function.IntPredicate;
import net.caramel.snare.setting.KeybindSetting;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public final class ModuleManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("snare/modules");
    private final LinkedHashMap<String, Module> modules = new LinkedHashMap<>();
    private final Map<String, Boolean> keyStates = new HashMap<>();
    public void register(Module module) { if (modules.putIfAbsent(module.id(), module) != null) throw new IllegalArgumentException("duplicate module id " + module.id()); }
    public List<Module> modules() { return List.copyOf(modules.values()); }
    public Map<ModuleCategory,List<Module>> groupedByCategory() {
        Map<ModuleCategory,List<Module>> grouped = new LinkedHashMap<>();
        modules.values().forEach(module -> grouped.computeIfAbsent(module.category(), ignored -> new ArrayList<>()).add(module));
        grouped.replaceAll((category, values) -> List.copyOf(values)); return Collections.unmodifiableMap(grouped);
    }
    public void tick(MinecraftClient client) { for (Module module : modules.values()) if (module.isEnabled()) try { module.onClientTick(client); } catch (RuntimeException e) { LOGGER.error("Module {} tick failed", module.id(), e); } }
    public void updateKeybinds(IntPredicate pressed, boolean screenOpen) {
        for (Module module : modules.values()) module.keybind().ifPresent(key -> {
            boolean down = key.value() != KeybindSetting.UNBOUND && pressed.test(key.value());
            boolean previous = keyStates.getOrDefault(module.id(), false); keyStates.put(module.id(), down);
            if (!screenOpen && down && !previous) module.toggle();
        });
    }
}