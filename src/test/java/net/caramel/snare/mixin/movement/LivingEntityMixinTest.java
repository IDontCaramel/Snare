package net.caramel.snare.mixin.movement;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import net.minecraft.entity.LivingEntity;
import org.junit.jupiter.api.Test;

class LivingEntityMixinTest {
    @Test
    void creativeFlightDragRedirectAcceptsTheInvokedEntity() {
        assertDoesNotThrow(() -> LivingEntityMixin.class.getDeclaredMethod(
                "snare$creativeFlightDrag", LivingEntity.class, double.class, double.class, double.class));
    }
}
