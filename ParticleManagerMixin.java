package com.autoboost.mixin;

import com.autoboost.BoostConfig;
import com.autoboost.Optimizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {
    // Patlama sırasında sadece UZAKTAKİ patlama/duman parçacıklarını atlar (yakındakiler aynen çizilir)
    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At("HEAD"), cancellable = true)
    private void autoboost$skipFar(ParticleEffect effect, double x, double y, double z,
                                   double vx, double vy, double vz,
                                   CallbackInfoReturnable<Particle> cir) {
        try {
            if (!Optimizer.explosionActive() || !BoostConfig.get().enabled) return;
            Object type = effect.getType();
            if (type != ParticleTypes.EXPLOSION && type != ParticleTypes.EXPLOSION_EMITTER
                    && type != ParticleTypes.POOF && type != ParticleTypes.SMOKE
                    && type != ParticleTypes.LARGE_SMOKE) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null && mc.player.squaredDistanceTo(x, y, z) > 12 * 12) {
                cir.setReturnValue(null);
            }
        } catch (Throwable ignored) { }
    }
}
