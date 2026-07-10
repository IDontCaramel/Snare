package net.caramel.snare.setting;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class SettingTest {
    @Test void numberClampsAndRoundsToStep() {
        NumberSetting value = new NumberSetting("offset", "Offset", "HUD offset", 4.0, 0.0, 10.0, 2.0);
        value.setValue(9.2); assertEquals(10.0, value.value());
        value.setValue(-3.0); assertEquals(0.0, value.value());
        value.setRatio(0.51); assertEquals(6.0, value.value());
    }

    @Test void modeRetainsValidValuesAndFallsBackForInvalidValues() {
        ModeSetting mode = new ModeSetting("style", "Style", "Label style", "Compact", List.of("Compact", "Verbose"));
        mode.setValue("Verbose"); assertEquals("Verbose", mode.value());
        mode.setValue("missing"); assertEquals("Compact", mode.value());
        mode.cycle(-1); assertEquals("Verbose", mode.value());
    }

    @Test void changesNotifyOnceAndVisibilityIsDynamic() {
        AtomicInteger changes = new AtomicInteger();
        boolean[] visible = {false};
        BooleanSetting setting = new BooleanSetting("show", "Show", "Show sample", true, () -> visible[0]);
        setting.setChangeListener(changes::incrementAndGet);
        setting.setValue(true); assertEquals(0, changes.get());
        setting.setValue(false); assertEquals(1, changes.get());
        assertFalse(setting.isVisible()); visible[0] = true; assertTrue(setting.isVisible());
    }

    @Test void invalidDefinitionsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new ModeSetting("m", "M", "", "x", List.of()));
        assertThrows(IllegalArgumentException.class, () -> new NumberSetting("n", "N", "", 0, 2, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new NumberSetting("n", "N", "", 0, 0, 1, 0));
    }
}