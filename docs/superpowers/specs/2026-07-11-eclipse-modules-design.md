# Eclipse Module Ports Design

## Scope

Port the nine remaining requested modules from Eclipse's Tweakeroo Additions to Snare. Reimplement each behavior against Minecraft 1.20.1 Yarn mappings and Snare's existing module, setting, config, and mixin infrastructure. Do not add Eclipse, Tweakeroo, MaLiLib, MixinExtras, or server-side support as dependencies.

The original server opt-in checks are intentionally omitted. Enabled modules apply their client-side behavior without checking whether the connected server advertises support.

## Modules

All modules are disabled by default, registered through `SnareModules`, and persisted by the existing config system.

| ID | Display name | Category | Settings and behavior |
|---|---|---|---|
| `slippery` | Slippery | Movement | Overrides local-player block friction. `Slipperiness`: 0.60-1.15, default 0.98. `Affect Vehicles`: default false. |
| `jump_velocity` | Jump Velocity | Movement | Overrides the local player's block jump factor. `Jump Velocity`: 0.50-5.00, default 1.15. |
| `boat_tweaks` | Boat Tweaks | Movement | Stops residual yaw when steering is released and lets a locally controlled boat jump forward obstacles. `Spider Boat`: default false, permits wall climbing. `Player Yaw`: default false, steers from the controlling player's yaw instead of left/right boat input. |
| `creative_elytra_flight` | Creative Elytra Flight | Movement | Enters creative-style flight when the local player starts elytra gliding and restores the prior flight ability afterward. |
| `gravity_tweaks` | Gravity Tweaks | Behavior | Overrides local-player gravity. `Gravity`: -0.50-0.50, default 0.08. |
| `step_height` | Step Height | Movement | Overrides local-player step height. `Step Height`: 0.00-1.50, default 0.60. |
| `horse_tweaks` | Horse Tweaks | Movement | Removes horse jump charging and cooldown and removes the camel dash cooldown. |
| `no_slow` | NoSlow | Movement | Removes local-player movement slowdown while using an item. |
| `no_jump_delay` | No Jump Delay | Removal | Removes the local player's jump cooldown. |

Numeric setting increments should use `0.01`, consistent with the precision of the requested values and existing Snare number settings.

## Architecture

Use target-oriented mixins, following Snare's existing pattern of looking up the relevant module through `SnareClient.modules()` at the injection point. Keep each injection inert if its module is absent or disabled.

- Entity and living-entity targets handle jump factor, block friction, and jump delay.
- The client-player target handles gravity, step height, item-use slowdown, and horse jump input and cooldown.
- The boat target handles optional vehicle friction, forward obstacle jumping, optional wall climbing, player-yaw steering, and residual-yaw cancellation.
- The player target handles creative elytra flight state.
- The camel target handles dash cooldown removal.

Use standard Sponge Mixin injectors available in the current project. Do not add MixinExtras solely to mirror the newer reference implementation. Injection locations and method names must be selected from Minecraft 1.20.1 bytecode and Yarn mappings rather than copied from the newer source.

## Behavioral Boundaries

Slippery affects only the local player. Its vehicle behavior applies only when `Affect Vehicles` is enabled and the local player controls the affected vehicle.

Boat Tweaks affects only a boat controlled by the local player. Basic forward obstacle jumping and residual-yaw cancellation are active with the module. Spider-style wall climbing and player-yaw steering remain independent settings and default to disabled.

Creative Elytra Flight records the player's previous `allowFlying` state and whether Snare activated creative flight. It restores the previous permission and clears Snare-enabled flight when gliding ends, the module is disabled, the player disconnects or is replaced, or the world is left. Cleanup must not remove legitimate creative or spectator flight permission.

Gravity accepts negative values intentionally, allowing upward acceleration. A value of 0.08 matches vanilla gravity. Step Height's default 0.60 matches vanilla player behavior.

Horse Tweaks applies full jump strength without charging, removes mounted-jump cooldown, and removes camel dash cooldown. NoSlow removes only item-use movement slowdown; it does not alter item use duration or other movement effects.

## Configuration And Data Flow

Each module owns its settings. Mixins read current values directly, so GUI and loaded-config changes take effect without duplicated state. Existing setting listeners request config saves, and no additional persistence format is required.

State is limited to Creative Elytra Flight's restoration data. That state belongs to its mixin or module lifecycle implementation and is reset whenever the associated local player is no longer valid.

## Failure Safety

Missing or disabled modules always fall back to vanilla behavior. Stateful cleanup for Creative Elytra Flight must run on module disable as well as normal glide termination. No packet protocol, server handshake, or server-presence detection is introduced.

## Verification

Do not add automated tests for this batch.

Run `./gradlew compileJava` and `./gradlew build`. Where feasible, use a dev client to smoke-check:

- local-player friction and optional controlled-vehicle friction;
- jump velocity and repeated jumping;
- boat obstacle jumping, yaw stopping, Spider Boat, and Player Yaw;
- creative elytra activation and restoration on every cleanup path;
- positive, zero, and negative gravity;
- step-height changes;
- immediate horse jumps and repeated camel dashes;
- normal movement speed while using items;
- vanilla behavior for every feature while its module is disabled.

## Out Of Scope

- The requested LiquidBounce modules and commands.
- Server-side opt-in, networking, or a Snare server component.
- New automated tests.
- A generic event bus or movement-override framework.
- Unrelated module, GUI, or config refactoring.
