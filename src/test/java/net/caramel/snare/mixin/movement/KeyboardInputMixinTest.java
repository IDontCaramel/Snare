package net.caramel.snare.mixin.movement;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import org.spongepowered.asm.mixin.Shadow;

class KeyboardInputMixinTest {
    @Test
    void doesNotShadowFieldsInheritedFromInput() {
        for (Field field : KeyboardInputMixin.class.getDeclaredFields()) {
            assertFalse(field.isAnnotationPresent(Shadow.class),
                    () -> field.getName() + " is inherited by KeyboardInput and cannot be shadowed");
        }
    }
}
