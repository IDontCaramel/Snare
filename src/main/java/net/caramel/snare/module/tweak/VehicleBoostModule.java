package net.caramel.snare.module.tweak;

import net.caramel.snare.event.EventBus;
import net.caramel.snare.event.EventPriority;
import net.caramel.snare.event.type.ClientDisconnectEvent;
import net.caramel.snare.event.type.ClientTickEvent;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;
import net.caramel.snare.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;

public final class VehicleBoostModule extends Module {
    public final NumberSetting horizontalSpeed = addSetting(new NumberSetting(
        "horizontal_speed", "Horizontal Speed", "", 2.0, 0.1, 10.0, 0.1
    ));
    public final NumberSetting verticalSpeed = addSetting(new NumberSetting(
        "vertical_speed", "Vertical Speed", "", 1.0, 0.1, 5.0, 0.1
    ));

    private ClientPlayerEntity trackedPlayer;
    private boolean wasMounted;

    public VehicleBoostModule(EventBus events, Runnable saveRequest) {
        super("vehicle_boost", "Vehicle Boost", "Boosts you when dismounting.", ModuleCategories.MOVEMENT, saveRequest);
        events.subscribe(ClientTickEvent.class, EventPriority.NORMAL, this::isEnabled, event -> {
            ClientPlayerEntity player = event.client().player;
            if (player == null) return;
            if (player != trackedPlayer) {
                trackedPlayer = player;
                wasMounted = false;
            }
            if (wasMounted && !player.hasVehicle()) {
                float yaw = player.getYaw();
                double h = horizontalSpeed.value();
                double v = verticalSpeed.value();
                player.setVelocity(
                    -Math.sin(Math.toRadians(yaw)) * h,
                    v,
                    Math.cos(Math.toRadians(yaw)) * h
                );
                wasMounted = false;
            } else {
                wasMounted = player.hasVehicle();
            }
        });
        events.subscribe(ClientDisconnectEvent.class, EventPriority.NORMAL, this::isEnabled, event -> {
            trackedPlayer = null;
            wasMounted = false;
        });
    }

    @Override
    protected void onDisable() {
        trackedPlayer = null;
        wasMounted = false;
    }
}
