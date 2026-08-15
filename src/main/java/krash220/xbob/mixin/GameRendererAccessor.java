package krash220.xbob.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

    @Accessor("confusionAnimationTick")
    int getTick();

    @Invoker("bobHurt")
    void callBobHurt(PoseStack mat, float partialTicks);

    @Invoker("bobView")
    void callBobView(PoseStack mat, float partialTicks);

    @Invoker("getFov")
    double callGetFov(Camera camera, float tickDelta, boolean changingFov);
}