package com.autoboost.mixin;

import com.autoboost.BoostConfig;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "getFramerateLimit", at = @At("RETURN"), cancellable = true)
    private void autoboost$limit(CallbackInfoReturnable<Integer> cir) {
        try {
            BoostConfig c = BoostConfig.get();
            MinecraftClient mc = (MinecraftClient) (Object) this;
            if (!c.enabled) return;
            if (!mc.isWindowFocused()) {
                // Pencere odakta değilken FPS'i düşür
                if (c.backgroundLimit) {
                    cir.setReturnValue(Math.min(cir.getReturnValue(), c.backgroundFps));
                }
            } else if (c.unlimitedFps && mc.world != null) {
                // 260 = oyunda "sınırsız" (Max Framerate: Unlimited)
                cir.setReturnValue(260);
            }
        } catch (Throwable ignored) { }
    }
}
