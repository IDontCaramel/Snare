package net.caramel.snare.module;

import net.caramel.snare.event.EventBus;
import net.caramel.snare.module.tweak.*;

public final class SnareModules {
    private SnareModules() {}

    public static void registerAll(ModuleManager manager, EventBus events, Runnable saveRequest) {
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
        manager.register(new SleepwalkerModule(events, saveRequest));
        manager.register(new VehicleBoostModule(events, saveRequest));
        manager.register(new PotionSpoofModule(events, saveRequest));
        manager.register(new SnapTapModule(events, saveRequest));
        manager.register(new BouncerModule(events, saveRequest));
        manager.register(new PortalMenuModule(events, saveRequest));
        manager.register(new CraftCarryModule(events, saveRequest));
        manager.register(new KeepBreakProgressModule(events, saveRequest));
        manager.register(new NoInterpolationModule(events, saveRequest));
        manager.register(new EntityControlModule(events, saveRequest));
        manager.register(new ElytraRecastModule(events, saveRequest));
    }
}