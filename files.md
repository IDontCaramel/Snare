# Snare Mod File Map

## Entry Point
- **`src/main/java/net/caramel/snare/SnareClient.java`** — Client entrypoint (`ClientModInitializer`). Registers the open-GUI keybind (`Y`), ticks modules unconditionally (even while screens are open), loads/saves config, and initializes module manager, terminal, and config manager. Registers disconnect/stop lifecycle cleanup for Creative Elytra Flight.

## Module Framework (`src/main/java/net/caramel/snare/module/`)
- **`Module.java`** — Abstract base class for all modules. Manages id/name/description/category, lifecycle (onEnable/onDisable/onClientTick), settings list, enabled state, and keybind.
- **`ModuleManager.java`** — Registers/deregisters modules, dispatches client ticks to enabled modules, handles keybind rising-edge toggling (disabled while GUI is open).
- **`ModuleCategory.java`** — Record with id and name for categorizing modules.
- **`ModuleCategories.java`** — Static category constants: MISC, MOVEMENT, REMOVAL, RENDER, BEHAVIOR, SPOOF.
- **`SnareModules.java`** — Production module catalog; registers all 19 built-in tweak modules.

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
- **`JumpVelocityModule.java`** — (MOVEMENT) "velocity" number; overrides block jump factor.
- **`BoatTweaksModule.java`** — (MOVEMENT) "spider_boat" and "player_yaw" booleans; enables forward obstacle climbing and yaw-based steering.
- **`CreativeElytraFlightModule.java`** — (MOVEMENT) Stateful lifecycle; switches an elytra glide into creative flight on begin(), manages abilities restoration on tick, disable, disconnect, and player replacement.
- **`GravityTweaksModule.java`** — (BEHAVIOR) "gravity" number; overrides local-player gravity.
- **`StepHeightModule.java`** — (MOVEMENT) "height" number; overrides automatic step height.
- **`HorseTweaksModule.java`** — (MOVEMENT) No settings; removes mounted jump charging and cooldowns.
- **`NoSlowModule.java`** — (MOVEMENT) No settings; removes item-use movement slowdown.
- **`NoJumpDelayModule.java`** — (REMOVAL) No settings; removes the local-player jump cooldown.

## Settings (`src/main/java/net/caramel/snare/setting/`)
- **`Setting.java`** — Abstract typed setting base. Has id/name/description/default/value, visibility predicate, change listener, normalize/reset.
- **`BooleanSetting.java`** — Boolean toggle setting.
- **`NumberSetting.java`** — Double setting with min/max/step clamping, provides ratio() and setRatio() for slider UI.
- **`ModeSetting.java`** — String setting with a fixed list of choices; supports cycle(direction).
- **`TextSetting.java`** — Raw string text setting.
- **`KeybindSetting.java`** — Integer key code setting; UNBOUND = -1.

## Config (`src/main/java/net/caramel/snare/config/`)
- **`ConfigManager.java`** — Persists module state to `snare.json` (enabled state + all settings). Load/save via Gson. Deferred save with 10-tick delay.

## GUI (`src/main/java/net/caramel/snare/gui/`)
- **`SnareScreen.java`** — ClickGUI screen built with vanilla widgets (no PanelStudio used directly). Two tabs: Command and Modules. Sidebar with category/module list, settings panel with boolean/number/mode/text/keybind controls.

## Terminal (`src/main/java/net/caramel/snare/terminal/`)
- **`TerminalState.java`** — Terminal-style UI state. Command history (up/down navigation), output buffer capped at 200 lines, stub command execution.

## Command (`src/main/java/net/caramel/snare/command/`)
- **`SpectatorTeleportCommand.java`** — `/spectatortp <player>` client command via Fabric API. Suggests online player names, sends SpectatorTeleportC2SPacket.

## Mixins (`src/main/java/net/caramel/snare/mixin/`)

### Spectator (`mixin/spectator/`)
- **`TeleportSpectatorMenuMixin.java`** — Injects into `TeleportSpectatorMenu` constructor to rebuild player list including spectators (gray/italic name) when module enabled.
- **`TeleportToSpecificPlayerSpectatorCommandMixin.java`** — Implements `SpectatorMenuEntry` interface; styles spectator names with gray/italic.
- **`SpectatorMenuEntry.java`** — Interface mixin for `snare$setSpectator` flag.

### Sign (`mixin/sign/`)
- **`AbstractSignEditScreenMixin.java`** — Modifies sign edit screen's `SelectionManager` predicate to allow unlimited text length; draws "!" overflow warnings using the client text renderer rather than an inherited field shadow.
- **`SignBlockEntityRendererMixin.java`** — Modifies sign text render width to `Integer.MAX_VALUE` when module enabled.

### Book (`mixin/book/`)
- **`BookEditScreenMixin.java`** — Replaces book edit screen's text length predicate with configurable character cap.

### Movement (`mixin/movement/`)
- **`LivingEntityMixin.java`** — Redirects creative flight drag velocity set; receives the invoked `LivingEntity` receiver and applies the configurable horizontal drag multiplier. Also adds slipperiness, gravity, and jump-delay overrides.
- **`EntityMixin.java`** — Cancellable HEAD injections for jump velocity multiplier and step height; both limited to `ClientPlayerEntity`.
- **`ClientPlayerEntityMixin.java`** — `@ModifyConstant` for item-use slowdown (NoSlow); `@Redirect` for full mount jump strength and zero mount jump cooldown (HorseTweaks).
- **`BoatEntityMixin.java`** — Vehicle slipperiness from SlipperyModule; yaw stopping and Player Yaw steering; forward obstacle jumping with Spider Boat wall climb.
- **`CamelEntityMixin.java`** — Redirects dash cooldown field read/write and cancels `getJumpCooldown` when HorseTweaks enabled.
- **`PlayerEntityMixin.java`** — `@Inject(method = "startFallFlying", at = @At("TAIL"))` triggers creative elytra flight transition on the local player.

### Render (`mixin/render/`)
- **`BackgroundRendererMixin.java`** — Cancels fog rendering and applies flat fog when FogControlModule enabled.
- **`WorldBorderMixin.java`** — Injects into `getBoundWest/North/East/South/getSize` to return +/-Double.MAX_VALUE when module enabled.
- **`OperatorBlockMixin.java`** — Injects into `BarrierBlock`/`LightBlock`/`StructureVoidBlock.getRenderType` to return `MODEL`.
- **`ClientWorldMixin.java`** — Suppresses block particles for operator blocks when module enabled.

### Inventory (`mixin/inventory/`)
- **`PlayerArmorSlotMixin.java`** — Allows any item insertion into armor slots and ignores binding curse for item extraction.

### Item (`mixin/item/`)
- **`ItemCooldownManagerMixin.java`** — Cancels `ItemCooldownManager.set()` when module enabled.

## Resources (`src/main/resources/`)
- **`fabric.mod.json`** — Mod metadata: id `snare`, client-only, entrypoint `SnareClient`, mixins `snare.mixins.json`.
- **`snare.mixins.json`** — Declares all 17 client mixins with compatibility level JAVA_17.
- **`assets/snare/lang/en_us.json`** — Translations: "Open Snare" keybind, "Snare" category.
- **`assets/snare/icon.png`** — Mod icon.
- **`assets/minecraft/models/block/`** — Custom block models for barrier, light (00-15), and structure_void (for OperatorBlocksModule rendering).

## Tests (`src/test/java/net/caramel/snare/`)
- **`setting/SettingTest.java`** — Tests number clamping/stepping, mode validation/cycling, change notification, visibility, and invalid definition rejection.
- **`module/ModuleManagerTest.java`** — Tests singleton lifecycle, duplicate rejection, registration order, keybind rising-edge behavior, and fault-tolerant ticking.
- **`config/ConfigManagerTest.java`** — Tests JSON round-trip with typed values, and graceful fallback for invalid/missing/malformed data.
- **`terminal/TerminalStateTest.java`** — Tests command submission, history navigation, and output buffer cap.
- **`mixin/movement/LivingEntityMixinTest.java`** — Verifies the creative-flight redirect handler accepts the invoked `LivingEntity` receiver required by Mixin.
- **`mixin/sign/AbstractSignEditScreenMixinTest.java`** — Verifies every sign-edit mixin field shadow is declared directly by `AbstractSignEditScreen`.
