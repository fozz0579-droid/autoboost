package com.autoboost.mixin;

import com.autoboost.Optimizer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    // Sadece FPS düşükken / patlama sırasında uzaktaki eşya, XP, TNT, kristal çizimini atlar
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void autoboost$cull(Entity entity, Frustum frustum, double x, double y, double z,
                                CallbackInfoReturnable<Boolean> cir) {
        try {
            if (Optimizer.shouldCull(entity, x, y, z)) {
                cir.setReturnValue(false);
            }
        } catch (Throwable ignored) { }
    }
}
