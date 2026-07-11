package net.caramel.snare.mixin.sign;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.lang.reflect.Field;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import org.junit.jupiter.api.Test;
import org.spongepowered.asm.mixin.Shadow;

class AbstractSignEditScreenMixinTest {
    @Test
    void shadowsOnlyFieldsDeclaredByTheDirectTarget() {
        for (Field field : AbstractSignEditScreenMixin.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(Shadow.class)) {
                assertDoesNotThrow(() -> AbstractSignEditScreen.class.getDeclaredField(field.getName()));
            }
        }
    }
}
