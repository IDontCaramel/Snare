package net.caramel.snare.module;
import java.util.List; import net.caramel.snare.setting.*; import net.minecraft.client.font.TextRenderer; import net.minecraft.client.gui.DrawContext; import org.lwjgl.glfw.GLFW;
public final class ExampleModule extends Module {
 public static final ModuleCategory CATEGORY=new ModuleCategory("example","Example");
 public final BooleanSetting showHud=addSetting(new BooleanSetting("show_hud","Show HUD","Render the sample while enabled",true));
 public final NumberSetting offset=addSetting(new NumberSetting("offset","HUD Offset","Horizontal sample offset",8,0,120,2));
 public final ModeSetting labelMode=addSetting(new ModeSetting("label_mode","Label Mode","Sample label variant","Compact",List.of("Compact","Verbose")));
 public final TextSetting labelText=addSetting(new TextSetting("label_text","Label Text","Text shown in the HUD","Snare active"));
 public final KeybindSetting toggleKey=addSetting(new KeybindSetting("toggle_key","Toggle Key","Gameplay module toggle",GLFW.GLFW_KEY_G));
 public ExampleModule(Runnable saveRequest){super("example_module","Example Module","Demonstrates every supported setting type.",CATEGORY,saveRequest);}
 public void renderHud(DrawContext context,TextRenderer renderer){if(!isEnabled()||!showHud.value())return; int x=6+offset.value().intValue(),y=6; context.fill(x,y,x+3,y+renderer.fontHeight,0xFF9B6CFF); String text=labelMode.value().equals("Verbose")?"[Snare] "+labelText.value():labelText.value(); context.drawTextWithShadow(renderer,text,x+6,y,0xFFE7E2F2);}
}