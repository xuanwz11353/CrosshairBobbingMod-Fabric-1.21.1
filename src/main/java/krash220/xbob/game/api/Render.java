package krash220.xbob.game.api;

import krash220.xbob.game.api.bus.GuiBus;
import krash220.xbob.game.api.math.MatrixStack;
import krash220.xbob.mixin.GameRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Render {

    public static int getScaledWidth() {
        return Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }

    public static int getScaledHeight() {
        return Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }

    public static int getBlitOffset() {
        return 0;
    }

    public static boolean isDebugCrosshair() {
        Minecraft mc = Minecraft.getInstance();

        return mc.options.hideGui || mc.getDebugOverlay().showDebugScreen() && !mc.player.isReducedDebugInfo() && !mc.options.reducedDebugInfo().get();
    }

    public static void bobView(MatrixStack mat, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        ((GameRendererAccessor) mc.gameRenderer).callBobHurt(mat.mat, partialTicks);
        if (mc.options.bobView().get().booleanValue()) {
            ((GameRendererAccessor) mc.gameRenderer).callBobView(mat.mat, partialTicks);
        }
    }

    private static Boolean coh = null;

    public static void modCamera(MatrixStack mat) {
        if (coh == null) {
            try {
                Class.forName("mirsario.cameraoverhaul.core.callbacks.ModifyCameraTransformCallback");
                coh = true;
            } catch (ClassNotFoundException e) {
                coh = false;
            }
        }

        if (coh) {
            try {
                var callbackClass = Class.forName("mirsario.cameraoverhaul.core.callbacks.ModifyCameraTransformCallback");
                var eventField = callbackClass.getField("EVENT");
                var event = eventField.get(null);
                var invokerMethod = event.getClass().getMethod("Invoker");
                var invoker = invokerMethod.invoke(event);
                var modifyMethod = invoker.getClass().getMethod("ModifyCameraTransform", Object.class, Object.class);

                var transformClass = Class.forName("mirsario.cameraoverhaul.core.structures.Transform");
                var t = modifyMethod.invoke(invoker, null, transformClass.getDeclaredConstructor().newInstance());

                var eulerRotField = transformClass.getField("eulerRot");
                var eulerRot = eulerRotField.get(t);
                var zField = eulerRot.getClass().getField("z");
                var xField = eulerRot.getClass().getField("x");
                var yField = eulerRot.getClass().getField("y");

                mat.rotate((float) zField.getDouble(eulerRot), 0, 0, 1);
                mat.rotate((float) xField.getDouble(eulerRot), 1, 0, 0);
                mat.rotate((float) yField.getDouble(eulerRot), 0, 1, 0);
            } catch (Exception e) {
                coh = false;
            }
        }
    }

    public static void updateCameraMatrix(MatrixStack mat, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        mat.mat.mulPose(mc.gameRenderer.getProjectionMatrix(((GameRendererAccessor) mc.gameRenderer).callGetFov(mc.gameRenderer.getMainCamera(), partialTicks, true)));
    }

    @SuppressWarnings("resource")
    public static float getReachDistance() {
        return 4.5f;
    }

    public static void distortion(MatrixStack mat, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();

        float g = mc.options.screenEffectScale().get().floatValue();
        float f = Mth.lerp(tickDelta, mc.player.oSpinningEffectIntensity, mc.player.spinningEffectIntensity) * g * g;
        if (f > 0.0F) {
           int i = mc.player.hasEffect(MobEffects.CONFUSION) ? 7 : 20;
           float f1 = 5.0F / (f * f + 5.0F) - f * 0.04F;
           f1 = f1 * f1;
           mat.rotate((((GameRendererAccessor) mc.gameRenderer).getTick() + tickDelta) * i, 0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F);
           mat.scale(1.0F / f1, 1.0F, 1.0F);
           float f2 = -(((GameRendererAccessor) mc.gameRenderer).getTick() + tickDelta) * i;
           mat.rotate(f2, 0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F);
        }
    }

    public static float getCenterDepth() {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.getCameraEntity();
        float partialTicks = GuiBus.partialTicks;

        if (entity != null && mc.level != null) {
            Vec3 vec3 = entity.getEyePosition(partialTicks);
            Vec3 vec3d2 = entity.getViewVector(partialTicks);
            Vec3 vec3d3 = vec3.add(vec3d2.x * 1000F, vec3d2.y * 1000F, vec3d2.z * 1000F);
            HitResult result = mc.level.clip(new ClipContext(vec3, vec3d3, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));

            if (result.getType() != HitResult.Type.MISS) {
                Vec3 begin = entity.getEyePosition(partialTicks);
                Vec3 end = result.getLocation();

                return (float) end.distanceTo(begin);
            }
        }

        return 1000F;
    }
}