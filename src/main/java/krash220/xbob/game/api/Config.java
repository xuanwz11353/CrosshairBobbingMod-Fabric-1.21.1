package krash220.xbob.game.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class Config {

    private static final String MOD_ID = "xbob";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, Boolean> DEFAULT = new LinkedHashMap<>();
    private static final Map<String, Boolean> CONFIG = new HashMap<>();

    private static File getConfigFile() {
        return new File(FMLPaths.CONFIGDIR.get().toFile(), MOD_ID + ".json");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void load() {
        File config = getConfigFile();

        if (!config.exists()) {
            save();
        }

        try (FileInputStream fis = new FileInputStream(config)) {
            Map map = GSON.fromJson(new InputStreamReader(fis, StandardCharsets.UTF_8), Map.class);

            CONFIG.putAll(map);
        } catch (IOException e) {}
    }

    public static void save() {
        String cfg = GSON.toJson(CONFIG);

        try (FileOutputStream fis = new FileOutputStream(getConfigFile())) {
            fis.write(cfg.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {}
    }

    public static void define(String key, Boolean defaultValue) {
        DEFAULT.put(key, defaultValue);
        CONFIG.put(key, defaultValue);
    }

    public static void set(String key, Boolean value) {
        CONFIG.put(key, value);
    }

    public static boolean check(String key) {
        Boolean val = CONFIG.get(key);

        if (val != null) {
            return val.booleanValue();
        } else {
            return DEFAULT.get(key).booleanValue();
        }
    }

    public static void registerGui() {
        ModContainer container = ModList.get().getModContainerById(MOD_ID).orElseThrow();

        Supplier<IConfigScreenFactory> supplier = () -> (modContainer, modListScreen) -> new ConfigScreen(modListScreen);
        container.registerExtensionPoint(IConfigScreenFactory.class, supplier);
    }

    private static class ConfigScreen extends Screen {

        private static final Component VALUE_ENABLE = Component.translatable("xbob.config.value.enable").withStyle(ChatFormatting.GREEN);
        private static final Component VALUE_DISABLE = Component.translatable("xbob.config.value.disable").withStyle(ChatFormatting.DARK_RED);

        public static Component getValueText(String key) {
            boolean bool = CONFIG.get(key).booleanValue();

            return Component.translatable("%s: %s", Component.translatable("xbob.config." + key), bool ? VALUE_ENABLE : VALUE_DISABLE);
        }

        private Screen previous;

        public ConfigScreen(Screen previous) {
            super(Component.translatable("xbob.config.title"));

            this.previous = previous;
        }

        @Override
        public void onClose() {
            save();

            Minecraft.getInstance().setScreen(this.previous);
        }

        @Override
        protected void init() {
            int i = this.height / 6 - 12;

            for (String key : DEFAULT.keySet()) {
                this.addRenderableWidget(Button.builder(getValueText(key), btn -> {
                    set(key, !check(key));

                    btn.setMessage(getValueText(key));
                }).pos(this.width / 2 - 150, i).size(300, 20).build());

                i += 24;
            }

            this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, btn -> this.onClose()).pos(this.width / 2 - 100, this.height - 27).size(200, 20).build());
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            this.renderBackground(graphics, mouseX, mouseY, partialTicks);
            graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
            super.render(graphics, mouseX, mouseY, partialTicks);
        }
    }
}