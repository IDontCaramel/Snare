package net.caramel.snare.module.tweak;

import net.caramel.snare.event.EventBus;
import net.caramel.snare.event.EventPriority;
import net.caramel.snare.event.type.ClientDisconnectEvent;
import net.caramel.snare.event.type.MovementInputEvent;
import net.caramel.snare.module.Module;
import net.caramel.snare.module.ModuleCategories;

public final class SnapTapModule extends Module {
    private final int[] pressOrder = new int[4];
    private final boolean[] previousPressed = new boolean[4];
    private int tickCounter = 0;

    public SnapTapModule(EventBus events, Runnable saveRequest) {
        super("snap_tap", "SnapTap", "Prioritizes the most recent movement key on each axis.", ModuleCategories.MOVEMENT, saveRequest);

        events.subscribe(MovementInputEvent.class, EventPriority.NORMAL, this::isEnabled, event -> {
            tickCounter++;
            boolean f = event.forward();
            boolean b = event.back();
            boolean l = event.left();
            boolean r = event.right();
            boolean[] current = { f, b, l, r };

            for (int i = 0; i < 4; i++) {
                if (current[i] && !previousPressed[i]) {
                    pressOrder[i] = tickCounter;
                } else if (!current[i]) {
                    pressOrder[i] = 0;
                }
                previousPressed[i] = current[i];
            }

            if (!event.screenOpen()) {
                if (current[0] && current[1]) {
                    if (pressOrder[0] < pressOrder[1]) {
                        event.forward(false);
                    } else {
                        event.back(false);
                    }
                }
                if (current[2] && current[3]) {
                    if (pressOrder[2] < pressOrder[3]) {
                        event.left(false);
                    } else {
                        event.right(false);
                    }
                }
            }
        });

        events.subscribe(ClientDisconnectEvent.class, EventPriority.NORMAL, () -> true, event -> reset());
    }

    @Override
    protected void onDisable() {
        reset();
    }

    private void reset() {
        for (int i = 0; i < 4; i++) {
            pressOrder[i] = 0;
            previousPressed[i] = false;
        }
        tickCounter = 0;
    }
}
