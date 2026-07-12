# Snare Mod File Map

## Entry Point
- **`src/main/java/net/caramel/snare/SnareClient.java`** — Client entrypoint (`ClientModInitializer`). Registers the open-GUI keybind (`Y`), ticks modules unconditionally (even while screens are open), loads/saves config, and initializes module manager, terminal, config manager, and event bus. Registers disconnect/stop lifecycle cleanup for Creative Elytra Flight. Posts `ClientTickEvent` and `ClientDisconnectEvent` on the event bus.

## Module Framework (`src/main/java/net/caramel/snare/module/`)
- **`Module.java`** — Abstract base class for all modules. Manages id/name/description/category, lifecycle (onEnable/onDisable/onClientTick), settings list, enabled state, and keybind.
- **`ModuleManager.java`** — Registers/deregisters modules, dispatches client ticks to enabled modules, handles keybind rising-edge toggling (disabled while GUI is open).
- **`ModuleCategory.java`** — Record with id and name for categorizing modules.
- **`ModuleCategories.java`** — Static category constants: MISC, MOVEMENT, REMOVAL, RENDER, BEHAVIOR, SPOOF.
- **`SnareModules.java`** — Production module catalog; registers all 30 modules (19 original + 11 new from event-based additions).

### Tweak Modules (`module/tweak/`)
- **`SpectatorTpModule.java`** — (MISC) "include_spectators" boolean setting; adds spectator-mode players to the teleport menu.
- **`NoFlyDragModule.java`** — (MOVEMENT) "drag" number setting (0-1); reduces creative-flight horizontal momentum.
- **`NoSignLimitModule.java`** — (REMOVAL) Removes client-side sign text width limits.
- **`NoArmorRestrictionModule.java`** — (REMOVAL) Allows any item in client armor slots.
- **`NoBindingCurseModule.java`** — (REMOVAL) Allows removal of binding-cursed armor client-side.
- **`BookTweaksModule.java`** — (REMOVAL) "character_cap" number setting (100-1024); raises book character limit per page.
- **`OperatorBlocksModule.java`** — (RENDER) Renders barrier/light/structure_void blocks as visible models; reloads world renderer on toggle.
- **`FogControlModule.java`** — (RENDER) Suppresses environment-specific fog.
- **`NoWorldBorderModule.java`** — (RENDER) Suppresses the client world border rendering/collision.
- **`NoItemCooldownModule.java`** — (BEHAVIOR) Suppresses newly applied item cooldowns.
- **`SlipperyModule.java`** — (MOVEMENT) "slipperiness" number and "affect_vehicles" boolean; overrides ground slipperiness for the local player and optionally their vehicle.
- **`JumpVelocityModule.java`** — (MOVEMENT) "velocity" number; overrides block jump factor via `PlayerJumpVelocityEvent`.
- **`BoatTweaksModule.java`** — (MOVEMENT) "spider_boat" and "player_yaw" booleans; enables forward obstacle climbing and yaw-based steering.
- **`CreativeElytraFlightModule.java`** — (MOVEMENT) Stateful lifecycle; switches an elytra glide into creative flight on begin(), manages abilities restoration on tick, disable, disconnect, and player replacement. Mutually exclusive with ElytraRecastModule.
- **`GravityTweaksModule.java`** — (BEHAVIOR) "gravity" number; overrides local-player gravity.
- **`StepHeightModule.java`** — (MOVEMENT) "height" number; overrides automatic step height.
- **`HorseTweaksModule.java`** — (MOVEMENT) No settings; removes mounted jump charging and cooldowns.
- **`NoSlowModule.java`** — (MOVEMENT) No settings; removes item-use movement slowdown.
- **`NoJumpDelayModule.java`** — (REMOVAL) No settings; removes the local-player jump cooldown.
- **`SleepwalkerModule.java`** — (BEHAVIOR) No settings; holds forward movement input while a GUI screen is open. Posts `MovementInputEvent` to override keyboard input.
- **`VehicleBoostModule.java`** — (MOVEMENT) "horizontal_speed" and "vertical_speed" number settings; boosts player velocity on vehicle dismount.
- **`PotionSpoofModule.java`** — (SPOOF) "potion_id" number and "amplifier" number; spoofs potion effect indicators by cancelling potion-related outgoing packets.
- **`SnapTapModule.java`** — (BEHAVIOR) No settings; overrides conflicting movement inputs so the most recent direction wins (like SnapTap). Posts `MovementInputEvent`.
- **`BouncerModule.java`** — (MOVEMENT) "bounce" number setting; applies upward velocity when landing.
- **`PortalMenuModule.java`** — (BEHAVIOR) No settings; keeps the current GUI screen open when entering a nether portal.
- **`CraftCarryModule.java`** — (BEHAVIOR) No settings; keeps the crafting table/other screen open after shift-clicking the result. Cancels specific `ClickSlotC2SPacket` via `PacketSendEvent`.
- **`KeepBreakProgressModule.java`** — (BEHAVIOR) No settings; preserves block breaking progress when switching held items. Cancels `cancelBlockBreaking` via `CancelBlockBreakingEvent`.
- **`NoInterpolationModule.java`** — (REMOVAL) No settings; sets entity interpolation steps to 0 for living entities, boats, and minecarts via `EntityInterpolationEvent`.
- **`EntityControlModule.java`** — (MOVEMENT) No settings; allows controlling unsaddled mounts (pigs, horses, striders) by overriding `isSaddled()` via `SaddleCheckEvent`, and overrides mount jump strength via `MountJumpStrengthEvent`.
- **`ElytraRecastModule.java`** — (MOVEMENT) No settings; automatically recasts elytra flight when the jump key is pressed after a glide ends. Uses `ClientTickEvent` for per-tick state tracking. Mutually exclusive with CreativeElytraFlightModule.

## Settings (`src/main/java/net/caramel/snare/setting/`)
- **`Setting.java`** — Abstract typed setting base. Has id/name/description/default/value, visibility predicate, change listener, normalize/reset.
- **`BooleanSetting.java`** — Boolean toggle setting.
- **`NumberSetting.java`** — Double setting with min/max/step clamping, provides ratio() and setRatio() for slider UI.
- **`ModeSetting.java`** — String setting with a fixed list of choices; supports cycle(direction).
- **`TextSetting.java`** — Raw string text setting.
- **`KeybindSetting.java`** — Integer key code setting; UNBOUND = -1.

## Event System (`src/main/java/net/caramel/snare/event/`)
- **`EventBus.java`** — Generic typed event bus. Supports `subscribe(Class<E>, EventPriority, BooleanSupplier, Consumer<E>)` and `post(E)`. Listeners are sorted by priority and filtered by active predicate.
- **`EventPriority.java`** — Enum: HIGHEST, HIGH, NORMAL, LOW, LOWEST.
- **`CancellableEvent.java`** — Base class for cancellable events; provides `setCancelled(boolean)` and `isCancelled()`.

### Event Types (`event/type/`)
- **`ClientTickEvent(MinecraftClient client)`** — Posted every client tick from `SnareClient`.
- **`ClientDisconnectEvent(MinecraftClient client)`** — Posted when the client disconnects from a server.
- **`MovementInputEvent(forward, back, left, right, screenOpen)`** — Mutable movement input state; posted from `KeyboardInputMixin` to allow SnapTap and Sleepwalker overrides.
- **`PlayerJumpVelocityEvent(LivingEntity entity, float velocity)`** — Mutable jump velocity; posted from `PlayerJumpMixin` for JumpVelocityModule.
- **`PacketSendEvent(Packet<?> packet)`** — Cancellable; posted from `ClientConnectionMixin.send()` for PotionSpoof and CraftCarry packet filtering.
- **`CancelBlockBreakingEvent`** — Cancellable; posted from `ClientPlayerInteractionManagerMixin.cancelBlockBreaking()` for KeepBreakProgress.
- **`PortalScreenCheckEvent(boolean blocksPortalClose)`** — Mutable; posted from `ClientPlayerPortalMixin` to keep screens open through portals.
- **`SaddleCheckEvent(Entity entity, boolean saddled)`** — Mutable; posted from `SaddleableEntityMixin.isSaddled()` for EntityControl.
- **`MountJumpStrengthEvent(ClientPlayerEntity player, float strength)`** — Mutable; posted from `ClientPlayerEntityMixin` mount jump for EntityControl.
- **`EntityInterpolationEvent(Entity entity, int steps)`** — Mutable; posted from interpolation mixins for NoInterpolation.

## Config (`src/main/java/net/caramel/snare/config/`)
- **`ConfigManager.java`** — Persists module state to `snare.json` (enabled state + all settings). Load/save via Gson. Deferred save with 10-tick delay.

## GUI (`src/main/java/net/caramel/snare/gui/`)
- **`SnareScreen.java`** — ClickGUI screen built with vanilla widgets. Two tabs: Command and Modules. Sidebar with category/module list, settings panel with boolean/number/mode/text/keybind controls.

## Terminal (`src/main/java/net/caramel/snare/terminal/`)
- **`TerminalState.java`** — Terminal-style UI state. Command history (up/down navigation), output buffer capped at 200 lines, delegates execution/suggestions to TerminalCommandRegistry.
- **`TerminalCommand.java`** — Interface for terminal commands: `name()`, `usage()`, `description()`, `execute(arguments, context)`, `suggest(input)`.
- **`TerminalCommandContext.java`** — Record holding `MinecraftClient client` and `Consumer<String> output`.
- **`TerminalSuggestion.java`** — Record with `replacement` (nullable; null = non-completable), `label`, and `description`.
- **`TerminalCommandRegistry.java`** — Registers commands, dispatches `execute()` and `suggest()` by matching first token to registered command names.

### Terminal Commands (`terminal/command/`)
- **`KickTerminalCommand.java`** — 6 kick modes: quit, invalid_packet, self_hurt, illegal_chat, illegal_switch_item, illegal_interact. Uses Yarn 1.20.1 C2S packets.
- **`DamageTerminalCommand.java`** — 3 damage modes (ncp, aac, verus) with per-mode packet logic. Amount 0-20.

## Command (`src/main/java/net/caramel/snare/command/`)
- **`SpectatorTeleportCommand.java`** — `/spectatortp <player>` client command via Fabric API. Suggests online player names, sends SpectatorTeleportC2SPacket.

## Mixins (`src/main/java/net/caramel/snare/mixin/`)

### Spectator (`mixin/spectator/`)
- **`TeleportSpectatorMenuMixin.java`** — Injects into `TeleportSpectatorMenu` constructor to rebuild player list including spectators (gray/italic name) when module enabled.
- **`TeleportToSpecificPlayerSpectatorCommandMixin.java`** — Implements `SpectatorMenuEntry` interface; styles spectator names with gray/italic.
- **`SpectatorMenuEntry.java`** — Interface mixin for `snare$setSpectator` flag.

### Sign (`mixin/sign/`)
- **`AbstractSignEditScreenMixin.java`** — Modifies sign edit screen's `SelectionManager` predicate to allow unlimited text length; draws "!" overflow warnings using the client text renderer.
- **`SignBlockEntityRendererMixin.java`** — Modifies sign text render width to `Integer.MAX_VALUE` when module enabled.

### Book (`mixin/book/`)
- **`BookEditScreenMixin.java`** — Replaces book edit screen's text length predicate with configurable character cap.

### Movement (`mixin/movement/`)
- **`LivingEntityMixin.java`** — Redirects creative flight drag velocity set; adds slipperiness, gravity, and jump-delay overrides.
- **`EntityMixin.java`** — Cancellable HEAD injections for jump velocity multiplier and step height; both limited to `ClientPlayerEntity`.
- **`ClientPlayerEntityMixin.java`** — `@ModifyConstant` for item-use slowdown (NoSlow); `@Redirect` for full mount jump strength and zero mount jump cooldown (HorseTweaks, EntityControl).
- **`BoatEntityMixin.java`** — Vehicle slipperiness, yaw stopping, Player Yaw steering, Spider Boat wall climb.
- **`CamelEntityMixin.java`** — Redirects dash cooldown field read/write and cancels `getJumpCooldown` when HorseTweaks enabled.
- **`PlayerEntityMixin.java`** — `@Inject(method = "startFallFlying", at = @At("TAIL"))` triggers creative elytra flight transition on the local player.
- **`KeyboardInputMixin.java`** — Posts `MovementInputEvent` from `KeyboardInput.tick()` for SnapTap and Sleepwalker input overrides.
- **`PlayerJumpMixin.java`** — Redirects `LivingEntity.getJumpVelocity()` to post `PlayerJumpVelocityEvent` for JumpVelocityModule.
- **`SaddleableEntityMixin.java`** — Injects `isSaddled()` return for pigs/horses/striders, posts `SaddleCheckEvent` for EntityControl.

### Render (`mixin/render/`)
- **`BackgroundRendererMixin.java`** — Cancels fog rendering and applies flat fog when FogControlModule enabled.
- **`WorldBorderMixin.java`** — Injects into `getBoundWest/North/East/South/getSize` to return +/-Double.MAX_VALUE when module enabled.
- **`OperatorBlockMixin.java`** — Injects into `BarrierBlock`/`LightBlock`/`StructureVoidBlock.getRenderType` to return `MODEL`.
- **`ClientWorldMixin.java`** — Suppresses block particles for operator blocks when module enabled.

### Inventory (`mixin/inventory/`)
- **`PlayerArmorSlotMixin.java`** — Allows any item insertion into armor slots and ignores binding curse for item extraction.

### Item (`mixin/item/`)
- **`ItemCooldownManagerMixin.java`** — Cancels `ItemCooldownManager.set()` when module enabled.

### Behavior (`mixin/behavior/`)
- **`ClientPlayerPortalMixin.java`** — Redirects `Screen.shouldPause()` in `ClientPlayerEntity.updateNausea()` to post `PortalScreenCheckEvent` for PortalMenu.
- **`ClientPlayerInteractionManagerMixin.java`** — Injects `cancelBlockBreaking()` HEAD with cancellation; posts `CancelBlockBreakingEvent` for KeepBreakProgress.

### Network (`mixin/network/`)
- **`ClientConnectionMixin.java`** — Injects `send()` HEAD with cancellation; posts `PacketSendEvent` for PotionSpoof and CraftCarry packet filtering.

### Interpolation (`mixin/interpolation/`)
- **`LivingEntityInterpolationMixin.java`** — Injects `updateTrackedPositionAndAngles()` TAIL and `updateTrackedHeadRotation()` TAIL to post `EntityInterpolationEvent` for NoInterpolation.
- **`BoatEntityInterpolationMixin.java`** — Injects `updateTrackedPositionAndAngles()` TAIL for boat interpolation override.
- **`AbstractMinecartEntityInterpolationMixin.java`** — Injects `updateTrackedPositionAndAngles()` TAIL for minecart interpolation override.

## Resources (`src/main/resources/`)
- **`fabric.mod.json`** — Mod metadata: id `snare`, client-only, entrypoint `SnareClient`, mixins `snare.mixins.json`.
- **`snare.mixins.json`** — Declares all 26 client mixins with compatibility level JAVA_17.
- **`assets/snare/lang/en_us.json`** — Translations: "Open Snare" keybind, "Snare" category.
- **`assets/snare/icon.png`** — Mod icon.
- **`assets/minecraft/models/block/`** — Custom block models for barrier, light (00-15), and structure_void (for OperatorBlocksModule rendering).

## Tests (`src/test/java/net/caramel/snare/`)
- **`setting/SettingTest.java`** — Tests number clamping/stepping, mode validation/cycling, change notification, visibility, and invalid definition rejection.
- **`module/ModuleManagerTest.java`** — Tests singleton lifecycle, duplicate rejection, registration order, keybind rising-edge behavior, and fault-tolerant ticking.
- **`config/ConfigManagerTest.java`** — Tests JSON round-trip with typed values, and graceful fallback for invalid/missing/malformed data.
- **`terminal/TerminalStateTest.java`** — Tests command submission, history navigation, and output buffer cap.
- **`mixin/movement/LivingEntityMixinTest.java`** — Verifies the creative-flight redirect handler accepts the invoked `LivingEntity` receiver required by Mixin.
- **`mixin/movement/KeyboardInputMixinTest.java`** — Prevents `KeyboardInputMixin` from shadowing fields inherited from `Input`, which Mixin cannot resolve on the direct target.
- **`mixin/sign/AbstractSignEditScreenMixinTest.java`** — Verifies every sign-edit mixin field shadow is declared directly by `AbstractSignEditScreen`.
