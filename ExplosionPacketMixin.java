package com.autoboost.mixin;

import com.autoboost.Optimizer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ExplosionPacketMixin {
    // Sunucudan patlama paketi gelince birkaç saniyelik koruma başlat
    @Inject(method = "onExplosion", at = @At("HEAD"))
    private void autoboost$explosion(ExplosionS2CPacket packet, CallbackInfo ci) {
        Optimizer.onExplosion();
    }
}
