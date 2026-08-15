package krash220.xbob.game.api.bus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import krash220.xbob.game.api.math.MatrixStack;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

public class GuiBus {

    public static float partialTicks;

    private static boolean registered = false;

    private static List<BiConsumer<MatrixStack, Float>> pre = new ArrayList<>();
    private static List<BiConsumer<MatrixStack, Float>> post = new ArrayList<>();

    public static void registerRenderCrosshair(BiConsumer<MatrixStack, Float> pre, BiConsumer<MatrixStack, Float> post) {
        if (!registered) {
            NeoForge.EVENT_BUS.addListener(GuiBus::preRender);
            NeoForge.EVENT_BUS.addListener(GuiBus::postRender);
            registered = true;
        }

        GuiBus.pre.add(pre);
        GuiBus.post.add(post);
    }

    public static void preRender(RenderGuiLayerEvent.Pre event) {
        if (event.getName().equals(VanillaGuiLayers.CROSSHAIR)) {
            partialTicks = event.getPartialTick().getGameTimeDeltaPartialTick(false);

            MatrixStack mat = new MatrixStack(event.getGuiGraphics().pose());

            GuiBus.doPre(mat, partialTicks);
        }
    }

    public static void postRender(RenderGuiLayerEvent.Post event) {
        if (event.getName().equals(VanillaGuiLayers.CROSSHAIR)) {
            partialTicks = event.getPartialTick().getGameTimeDeltaPartialTick(false);

            MatrixStack mat = new MatrixStack(event.getGuiGraphics().pose());

            GuiBus.doPost(mat, partialTicks);
        }
    }

    public static void doPre(MatrixStack mat, float partialTicks) {
        for (BiConsumer<MatrixStack, Float> handler : pre) {
            handler.accept(mat, partialTicks);
        }
    }

    public static void doPost(MatrixStack mat, float partialTicks) {
        for (BiConsumer<MatrixStack, Float> handler : post) {
            handler.accept(mat, partialTicks);
        }
    }
}