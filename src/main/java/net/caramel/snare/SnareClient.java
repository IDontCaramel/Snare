package net.caramel.snare;
import net.caramel.snare.command.SpectatorTeleportCommand; import net.caramel.snare.config.ConfigManager; import net.caramel.snare.gui.SnareScreen; import net.caramel.snare.module.*; import net.caramel.snare.module.tweak.CreativeElytraFlightModule; import net.caramel.snare.terminal.TerminalState;
import net.fabricmc.api.ClientModInitializer; import net.fabricmc.fabric.api.client.event.lifecycle.v1.*; import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper; import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents; import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding; import net.minecraft.client.util.InputUtil; import org.lwjgl.glfw.GLFW;
public final class SnareClient implements ClientModInitializer {
 private static final ModuleManager MODULES=new ModuleManager(); private static final TerminalState TERMINAL=new TerminalState();
 private static final ConfigManager CONFIG=new ConfigManager(FabricLoader.getInstance().getConfigDir().resolve("snare.json"));
 public static ModuleManager modules(){return MODULES;} public static ConfigManager config(){return CONFIG;} public static TerminalState terminal(){return TERMINAL;}
 @Override public void onInitializeClient(){
  Runnable saveRequest=()->CONFIG.requestSave(MODULES); SnareModules.registerAll(MODULES,saveRequest); SpectatorTeleportCommand.register(); CONFIG.load(MODULES);
  KeyBinding open=KeyBindingHelper.registerKeyBinding(new KeyBinding("key.snare.open",InputUtil.Type.KEYSYM,GLFW.GLFW_KEY_Y,"key.category.snare"));
  ClientTickEvents.END_CLIENT_TICK.register(client->{while(open.wasPressed())if(client.currentScreen==null)client.setScreen(new SnareScreen(MODULES,TERMINAL,CONFIG)); boolean screenOpen=client.currentScreen!=null; long handle=client.getWindow().getHandle(); MODULES.updateKeybinds(code->InputUtil.isKeyPressed(handle,code),screenOpen); MODULES.tick(client); CONFIG.tick();});
  ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->MODULES.get(CreativeElytraFlightModule.class).ifPresent(CreativeElytraFlightModule::reset));
  ClientLifecycleEvents.CLIENT_STOPPING.register(client->{MODULES.get(CreativeElytraFlightModule.class).ifPresent(CreativeElytraFlightModule::reset); CONFIG.flush();});
 }
}