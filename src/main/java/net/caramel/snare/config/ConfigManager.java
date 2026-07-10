package net.caramel.snare.config;
import com.google.gson.*; import java.io.*; import java.nio.charset.StandardCharsets; import java.nio.file.*; import java.util.*;
import net.caramel.snare.module.Module; import net.caramel.snare.module.ModuleManager; import net.caramel.snare.setting.*; import org.slf4j.*;
public final class ConfigManager {
    private static final Logger LOGGER=LoggerFactory.getLogger("snare/config"); private static final Gson GSON=new GsonBuilder().setPrettyPrinting().create(); private static final int SAVE_DELAY_TICKS=10;
    private final Path path; private ModuleManager pendingManager; private int ticksUntilSave=-1;
    public ConfigManager(Path path){this.path=Objects.requireNonNull(path);}
    public void requestSave(ModuleManager manager){pendingManager=Objects.requireNonNull(manager); ticksUntilSave=SAVE_DELAY_TICKS;}
    public void tick(){if(ticksUntilSave>=0 && --ticksUntilSave<=0) flush();}
    public void flush(){if(pendingManager==null)return; save(pendingManager); pendingManager=null; ticksUntilSave=-1;}
    public void load(ModuleManager manager){
        if(!Files.exists(path))return;
        try(Reader reader=Files.newBufferedReader(path,StandardCharsets.UTF_8)){
            JsonElement rootElement=JsonParser.parseReader(reader); if(!rootElement.isJsonObject())return;
            JsonObject modules=object(rootElement.getAsJsonObject().get("modules")); if(modules==null)return;
            for(Module module:manager.modules()){
                JsonObject data=object(modules.get(module.id())); if(data==null)continue;
                JsonObject settings=object(data.get("settings")); if(settings!=null) for(Setting<?> setting:module.settings()) apply(setting,settings.get(setting.id()));
            }
            for(Module module:manager.modules()){
                JsonObject data=object(modules.get(module.id())); if(data==null)continue;
                JsonElement enabled=data.get("enabled"); if(enabled!=null && enabled.isJsonPrimitive() && enabled.getAsJsonPrimitive().isBoolean()) module.setEnabled(enabled.getAsBoolean());
            }
        }catch(IOException|JsonParseException|IllegalStateException e){LOGGER.error("Could not load {}",path,e);}
    }
    private static JsonObject object(JsonElement value){return value!=null && value.isJsonObject()?value.getAsJsonObject():null;}
    private static void apply(Setting<?> setting,JsonElement value){
        if(value==null||!value.isJsonPrimitive())return; JsonPrimitive p=value.getAsJsonPrimitive();
        try{if(setting instanceof BooleanSetting s && p.isBoolean())s.setValue(p.getAsBoolean()); else if(setting instanceof NumberSetting s && p.isNumber())s.setValue(p.getAsDouble()); else if(setting instanceof ModeSetting s && p.isString())s.setValue(p.getAsString()); else if(setting instanceof TextSetting s && p.isString())s.setValue(p.getAsString()); else if(setting instanceof KeybindSetting s && p.isNumber())s.setValue(p.getAsInt());}catch(RuntimeException ignored){}
    }
    private void save(ModuleManager manager){
        JsonObject modules=new JsonObject(); for(Module module:manager.modules()){JsonObject data=new JsonObject(); data.addProperty("enabled",module.isEnabled()); JsonObject settings=new JsonObject(); for(Setting<?> setting:module.settings())add(settings,setting); data.add("settings",settings); modules.add(module.id(),data);} JsonObject root=new JsonObject(); root.add("modules",modules);
        try{Path parent=path.getParent(); if(parent!=null)Files.createDirectories(parent); Files.writeString(path,GSON.toJson(root),StandardCharsets.UTF_8,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);}catch(IOException e){LOGGER.error("Could not save {}",path,e);}
    }
    private static void add(JsonObject object,Setting<?> setting){Object value=setting.value(); if(value instanceof Boolean b)object.addProperty(setting.id(),b); else if(value instanceof Number n)object.addProperty(setting.id(),n); else object.addProperty(setting.id(),String.valueOf(value));}
}