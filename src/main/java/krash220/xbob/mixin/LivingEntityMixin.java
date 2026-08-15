package krash220.xbob.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import krash220.xbob.game.api.bus.PlayerBus;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "hurt", at = @At("HEAD"))
    public void onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> ci) {
        PlayerBus.onAttack(((LivingEntity) (Object) this), source, amount);
    }
}