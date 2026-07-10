# Client GUI And Module Framework Design

## Goal

Add a client-only Snare GUI that opens with `Y`, provides Command and Modules tabs, and establishes a reusable framework for registering modules and typed settings. The first release includes a functional example module and persistent client configuration, but not command execution.

## Scope

This feature includes:

- A client entry point and `Y` key binding.
- A custom two-tab Minecraft screen.
- A reusable module registry and module lifecycle.
- Boolean, number, mode, text, and keybind settings.
- Client-side JSON persistence for module and setting state.
- A terminal-like command interface with session history and placeholder responses.
- One example module demonstrating every supported setting type and a visible HUD effect.

This feature does not include:

- Executable commands or a command registry.
- Networking, packets, or server-owned state.
- A third-party GUI dependency.
- Persistent command history.
- A color setting or color picker.

## Client Architecture

Snare will be explicitly client-only. Its Fabric metadata will expose a client entry point backed by a `ClientModInitializer`; no server APIs or server entry point will be required.

The client initializer will:

1. Create the module manager.
2. Register the example module.
3. Load module and setting values from the client config.
4. Register the `Y` GUI key binding.
5. Register client tick handling for opening the screen, module keybinds, and enabled module tick hooks.
6. Register HUD rendering used by enabled modules such as the example module.

### Module Manager

`ModuleManager` owns registered modules, rejects duplicate module identifiers, preserves registration order for stable display, and provides modules grouped by category. Modules register through one API rather than being hardcoded into the GUI.

### Module

Each module has a stable identifier, display name, description, category, enabled state, optional module keybind, and ordered settings. A module exposes overridable lifecycle hooks for enable, disable, and client tick behavior.

Changing enabled state is idempotent: enabling an enabled module or disabling a disabled module does nothing. A real transition updates state, invokes the matching lifecycle hook once, and requests a config save. Loading persisted state initializes modules through the same state transition contract after all settings have loaded.

### Settings

All settings share a stable identifier, display name, description, default value, current value, and visibility contract. The initial concrete types are:

- Boolean: `true` or `false`.
- Number: bounded numeric value with minimum, maximum, and step; assignments are clamped and rounded to the configured step.
- Mode: one value from a fixed non-empty list.
- Text: a string value.
- Keybind: a client keyboard key used to toggle its owning module during gameplay.

Module keybinds only fire when no Minecraft screen is open. They therefore do not trigger while typing in chat, editing a text setting, capturing a keybind, or using another menu. A key transition toggles once and does not repeat every tick while held.

## Screen Behavior

Pressing `Y` during normal gameplay opens a centered `SnareScreen`. The screen releases the mouse cursor and blocks normal movement input through Minecraft's standard `Screen` behavior. `shouldPause()` returns `true`, pausing an integrated single-player world while leaving multiplayer behavior subject to normal Minecraft rules.

`Esc` closes the screen. The registered `Y` action only opens the screen while no screen is active; it is not a second close shortcut.

The GUI scales to the current window size. Its outer bounds remain centered and are constrained to fit smaller supported GUI scales. Content that exceeds available vertical space scrolls within its own panel rather than extending outside the window.

## Visual Language

The interface uses vanilla Minecraft text rendering and crisp rectangular geometry. Near-black and charcoal surfaces, thin pixel-like borders, compact spacing, and a restrained purple accent create a minimal dark interface that still belongs in Minecraft.

The design avoids gradients, rounded web-style cards, heavy shadows, and external textures. Hover, focus, selection, enabled state, and active tabs remain distinguishable through border, text, background, and purple-accent changes rather than animation-heavy effects.

## Tabs

A top bar presents `Command` and `Modules`. Clicking a tab changes the active content view. The active tab has a purple indicator and stronger text contrast. The screen initially opens to the last tab selected during the current game session; the initial session default is `Modules`.

### Command Tab

The Command tab contains a scrollable output region and a prompt text field at the bottom.

- `Enter` submits non-blank input.
- Submitted text appears in output and is added to session history.
- Every submitted command currently produces a clear `Command system not implemented yet.` response.
- Up and down arrows navigate submitted commands, including returning to a blank current prompt after the newest history entry.
- Output scroll supports the mouse wheel and remains bounded so old entries cannot grow memory indefinitely; the initial limit is 200 output lines.
- Terminal output and input history reset when the Minecraft client exits and are not stored in config.

### Modules Tab

The Modules tab is split into a category sidebar and a larger settings panel.

The sidebar lists categories in module registration order. Clicking a category header toggles its expanded state. Expanded categories reveal their modules directly below the header; multiple categories may remain expanded at once. Sidebar content scrolls independently when needed.

Each module row has two separate targets:

- The module name selects it and displays its details in the settings panel.
- A compact toggle enables or disables it without changing the selected module.

The selected module row has a visible purple selection treatment. If no module is selected, the settings panel displays a short selection prompt. If a selected module has no settings, it displays its name, description, enabled state, and a `No settings` message.

The right panel shows the selected module's display name, description, enabled state, and settings in registration order. It scrolls independently when its controls exceed available height.

## Setting Controls

- Boolean settings use a labeled on/off switch.
- Number settings use a draggable slider and display the current numeric value. Keyboard-independent pointer dragging is clamped to the slider bounds.
- Mode settings use a compact selector that cycles forward on left click and backward on right click.
- Text settings use a focused Minecraft text field and accept standard editing shortcuts supported by the underlying text field behavior.
- Keybind settings enter capture mode when clicked. The next keyboard key is stored; `Esc` cancels capture without changing the existing binding. A dedicated clear action sets the binding to unbound.

Control changes apply immediately. A debounced config save coalesces rapid changes such as slider dragging and text entry, while screen close and client shutdown flush pending changes.

## Example Module

An `Example` category contains an `Example Module`. It includes one setting of each supported type:

- A boolean controlling whether its HUD sample is shown while the module is enabled.
- A number controlling a visible aspect such as HUD offset.
- A mode controlling the sample label variant.
- Text used in the HUD sample.
- A keybind that toggles the module during gameplay.

While enabled and configured to show its sample, the module renders a small vanilla-font status line with a purple accent on the client HUD. This effect is local to the client and does not modify or communicate with the server.

## Persistence

`ConfigManager` stores JSON in Fabric Loader's client config directory. The document is keyed by stable module and setting identifiers, not display names, so labels can change without losing configuration.

The persisted data contains each module's enabled state and supported setting values. UI-only state such as expanded categories, selected module, active tab, scroll offsets, terminal history, and keybind capture state is not persisted.

Loading validates each value independently:

- Wrong JSON types are ignored.
- Numbers are clamped and step-rounded.
- Unknown mode values fall back to the setting default.
- Unknown modules and settings are ignored for forward and backward config compatibility.
- Missing modules and settings retain defaults.

A missing config file is normal and starts with defaults. Malformed files and read/write failures are logged without crashing the client. When possible, one invalid value does not prevent other valid values from loading.

## Input And Error Handling

Mouse hit testing is restricted to visible clipped regions. Unsupported mouse buttons and keyboard keys are ignored safely. Scroll offsets are clamped whenever content or window size changes. Resizing or changing GUI scale preserves valid selection while recalculating layout.

Only one control owns keyboard focus at a time. Switching tabs, selecting another module, or closing the screen clears text and keybind-capture focus safely. `Esc` first cancels active keybind capture; otherwise it closes the screen.

Module lifecycle exceptions are logged and isolated so a faulty module does not crash GUI interaction or prevent other module ticks. Config failures do not roll back in-memory setting changes.

## Testing And Verification

Automated tests will cover model behavior that is independent of Minecraft rendering:

- Module lifecycle hooks run once per real state transition.
- Duplicate module identifiers are rejected.
- Category grouping and registration order are stable.
- Number settings clamp and round correctly.
- Mode settings reject invalid values and retain valid values.
- Config serialization and loading preserve valid state.
- Missing, unknown, malformed, and individually invalid config values fall back safely.

Gradle build verification must pass. A manual client launch check will verify:

1. `Y` opens the GUI only from gameplay.
2. The mouse is free, movement is blocked, and integrated single-player pauses.
3. `Esc` closes the screen, except when first canceling keybind capture.
4. Both tabs switch and render correctly at multiple window sizes and GUI scales.
5. Terminal submission, placeholder output, history navigation, and scrolling work.
6. Categories expand, module names select, and toggles enable independently.
7. Every setting control updates the example module immediately.
8. The example module HUD line responds to its settings.
9. Module keybinds toggle once per press during gameplay and never while a screen is open.
10. Module and setting values survive a client restart.

## Success Criteria

The feature is complete when the client can open a polished, minimal Minecraft-style GUI with `Y`; navigate both tabs; interact with a real extensible module framework; configure and toggle the example module; observe its HUD effect; and recover the same module and setting state after restart without any server dependency.
