package net.caramel.snare.module;

import net.caramel.snare.module.tweak.*;

public final class SnareModules {
    private SnareModules() {}

    public static void registerAll(ModuleManager manager, Runnable saveRequest) {
        manager.register(new SpectatorTpModule(saveRequest));
        manager.register(new NoFlyDragModule(saveRequest));
        manager.register(new NoSignLimitModule(saveRequest));
        manager.register(new NoArmorRestrictionModule(saveRequest));
        manager.register(new NoBindingCurseModule(saveRequest));
        manager.register(new BookTweaksModule(saveRequest));
        manager.register(new OperatorBlocksModule(saveRequest));
        manager.register(new FogControlModule(saveRequest));
        manager.register(new NoWorldBorderModule(saveRequest));
        manager.register(new NoItemCooldownModule(saveRequest));
        manager.register(new SlipperyModule(saveRequest));
        manager.register(new JumpVelocityModule(saveRequest));
        manager.register(new BoatTweaksModule(saveRequest));
        manager.register(new CreativeElytraFlightModule(saveRequest));
        manager.register(new GravityTweaksModule(saveRequest));
        manager.register(new StepHeightModule(saveRequest));
        manager.register(new HorseTweaksModule(saveRequest));
        manager.register(new NoSlowModule(saveRequest));
        manager.register(new NoJumpDelayModule(saveRequest));
    }
}