package krash220.xbob.platform;

import java.lang.reflect.InvocationTargetException;

import net.neoforged.fml.common.Mod;

@Mod("xbob")
public class NeoForgeLoader {

    public NeoForgeLoader() {
        try {
            Class.forName("krash220.xbob.MainMod").getConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}