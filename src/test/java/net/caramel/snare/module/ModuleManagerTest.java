package net.caramel.snare.module;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import net.caramel.snare.setting.KeybindSetting;
import org.junit.jupiter.api.Test;

class ModuleManagerTest {
    private static final ModuleCategory A = new ModuleCategory("a", "A");
    static final class Probe extends Module {
        int enables, disables, ticks; boolean failTick;
        Probe(String id, ModuleCategory category, Runnable save) { super(id, id, id, category, save); addSetting(new KeybindSetting("key", "Key", "", 65)); }
        @Override protected void onEnable() { enables++; }
        @Override protected void onDisable() { disables++; }
        @Override public void onClientTick(net.minecraft.client.MinecraftClient client) { if (failTick) throw new IllegalStateException("broken tick"); ticks++; }
    }
    @Test void lifecycleRunsOncePerRealTransition() {
        AtomicInteger saves = new AtomicInteger(); Probe p = new Probe("one", A, saves::incrementAndGet);
        p.setEnabled(true); p.setEnabled(true); p.setEnabled(false); p.setEnabled(false);
        assertEquals(1, p.enables); assertEquals(1, p.disables); assertEquals(2, saves.get());
    }
    @Test void rejectsDuplicatesAndPreservesRegistrationAndCategoryOrder() {
        ModuleManager manager = new ModuleManager(); ModuleCategory b = new ModuleCategory("b", "B");
        Probe one = new Probe("one", A, () -> {}), two = new Probe("two", b, () -> {}), three = new Probe("three", A, () -> {});
        manager.register(one); manager.register(two); manager.register(three);
        assertThrows(IllegalArgumentException.class, () -> manager.register(new Probe("one", b, () -> {})));
        assertEquals(List.of(one, two, three), manager.modules());
        assertEquals(List.of(A, b), new ArrayList<>(manager.groupedByCategory().keySet()));
        assertEquals(List.of(one, three), manager.groupedByCategory().get(A));
    }
    @Test void keybindUsesRisingEdgesAndIsDisabledWhileScreenOpen() {
        ModuleManager manager = new ModuleManager(); Probe p = new Probe("one", A, () -> {}); manager.register(p);
        manager.updateKeybinds(code -> true, false); manager.updateKeybinds(code -> true, false);
        assertTrue(p.isEnabled());
        manager.updateKeybinds(code -> false, false); manager.updateKeybinds(code -> true, true);
        assertTrue(p.isEnabled());
        manager.updateKeybinds(code -> false, false); manager.updateKeybinds(code -> true, false);
        assertFalse(p.isEnabled());
    }
    @Test void faultyTickDoesNotStopOtherEnabledModules() {
        ModuleManager manager = new ModuleManager(); Probe broken = new Probe("broken", A, () -> {}), healthy = new Probe("healthy", A, () -> {});
        broken.failTick = true; broken.setEnabled(true); healthy.setEnabled(true); manager.register(broken); manager.register(healthy);
        assertDoesNotThrow(() -> manager.tick(null)); assertEquals(1, healthy.ticks);
    }
}