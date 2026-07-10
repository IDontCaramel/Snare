package net.caramel.snare.config;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*; import java.util.List;
import net.caramel.snare.module.Module; import net.caramel.snare.module.ModuleCategory; import net.caramel.snare.module.ModuleManager; import net.caramel.snare.setting.*;
import org.junit.jupiter.api.*; import org.junit.jupiter.api.io.TempDir;
class ConfigManagerTest {
    @TempDir Path dir;
    static final ModuleCategory CATEGORY = new ModuleCategory("test", "Test");
    static final class Fixture extends Module {
        final BooleanSetting flag=addSetting(new BooleanSetting("flag","Flag","",true));
        final NumberSetting number=addSetting(new NumberSetting("number","Number","",2,0,10,2));
        final ModeSetting mode=addSetting(new ModeSetting("mode","Mode","","a",List.of("a","b")));
        final TextSetting text=addSetting(new TextSetting("text","Text","","default"));
        Fixture(Runnable save){super("fixture","Fixture","",CATEGORY,save);}
    }
    @Test void roundTripPreservesTypedValuesAndEnabledState() {
        Path file=dir.resolve("snare.json"); ConfigManager writer=new ConfigManager(file); ModuleManager first=new ModuleManager(); Fixture source=new Fixture(() -> writer.requestSave(first)); first.register(source);
        source.flag.setValue(false); source.number.setValue(9.1); source.mode.setValue("b"); source.text.setValue("saved"); source.setEnabled(true); writer.flush();
        ConfigManager reader=new ConfigManager(file); ModuleManager second=new ModuleManager(); Fixture loaded=new Fixture(() -> reader.requestSave(second)); second.register(loaded); reader.load(second);
        assertAll(() -> assertFalse(loaded.flag.value()), () -> assertEquals(10,loaded.number.value()), () -> assertEquals("b",loaded.mode.value()), () -> assertEquals("saved",loaded.text.value()), () -> assertTrue(loaded.isEnabled()));
    }
    @Test void invalidUnknownAndMalformedDataFallsBackIndependently() throws Exception {
        Path file=dir.resolve("snare.json"); Files.writeString(file,"{\"modules\":{\"unknown\":{},\"fixture\":{\"enabled\":\"yes\",\"settings\":{\"flag\":7,\"number\":99,\"mode\":\"bad\",\"text\":\"ok\",\"extra\":true}}}}");
        ConfigManager config=new ConfigManager(file); ModuleManager manager=new ModuleManager(); Fixture loaded=new Fixture(() -> config.requestSave(manager)); manager.register(loaded); config.load(manager);
        assertTrue(loaded.flag.value()); assertEquals(10,loaded.number.value()); assertEquals("a",loaded.mode.value()); assertEquals("ok",loaded.text.value()); assertFalse(loaded.isEnabled());
        Files.writeString(file,"not json"); assertDoesNotThrow(() -> config.load(manager)); Files.delete(file); assertDoesNotThrow(() -> config.load(manager));
    }
}