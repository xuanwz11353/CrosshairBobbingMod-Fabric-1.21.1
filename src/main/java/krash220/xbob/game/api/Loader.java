package krash220.xbob.game.api;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;

public class Loader {

    public static String getPlatform() {
        return "NeoForge";
    }

    public static String getVersion() {
        return ModList.get().getModContainerById("minecraft")
            .map(c -> c.getModInfo().getVersion().toString())
            .orElse("unknown");
    }

    public static boolean isClient() {
        return FMLEnvironment.dist.isClient();
    }
}