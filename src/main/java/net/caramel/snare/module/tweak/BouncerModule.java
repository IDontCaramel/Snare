package net.caramel.snare.module.tweak;

import net.caramel.snare.event.EventBus;
import net.caramel.snare.event.EventPriority;
import net.caramel.snare.event.type.PlayerJumpVelocityEvent;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.caramel.snare.setting.NumberSetting;
import net.minecraft.block.BedBlock;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.SlimeBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public final class BouncerModule extends Module {
    public final NumberSetting motion = addSetting(new NumberSetting(
        "motion", "Motion", "", 0.42, 0.2, 2.0, 0.01
    ));

    public BouncerModule(EventBus events, Runnable saveRequest) {
        super("bouncer", "Bouncer", "Adds extra bounce velocity on certain blocks.", ModuleCategories.MOVEMENT, saveRequest);

        events.subscribe(PlayerJumpVelocityEvent.class, EventPriority.NORMAL, this::isEnabled, event -> {
            if (event.entity() != MinecraftClient.getInstance().player) return;

            Box box = event.entity().getBoundingBox();
            double minY = box.minY;
            Box detection = new Box(box.minX, minY - 0.01, box.minZ, box.maxX, minY, box.maxZ);

            for (BlockPos pos : BlockPos.stream(detection).map(BlockPos::toImmutable).toList()) {
                var block = event.entity().getWorld().getBlockState(pos).getBlock();
                if (block instanceof SlimeBlock || block instanceof BedBlock || block instanceof HoneyBlock) {
                    event.velocity(event.velocity() + motion.value().floatValue());
                    break;
                }
            }
        });
    }
}
