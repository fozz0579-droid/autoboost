package com.autoboost;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.TntEntity;

public class Optimizer {
    public static volatile boolean cullActive = false;
    public static volatile double cullDistSq = 28 * 28;

    // Patlama algılanınca birkaç saniyelik hızlı koruma
    public static volatile long explosionUntil = 0;
    public static final double EXPLOSION_CULL_SQ = 14 * 14;

    // HUD için
    public static volatile int fpsNow = 0;
    public static volatile int fpsDelta = 0;
    public static volatile int effectiveTarget = 60;

    private static final int[] history = new int[30];
    private static int histPos = 0, histCount = 0;
    private static int lastFps = -1;

    private static int ticks = 0;
    private static long lastGc = 0;
    private static boolean failed = false;

    public static boolean explosionActive() {
        return System.currentTimeMillis() < explosionUntil;
    }

    public static void onExplosion() {
        try {
            BoostConfig c = BoostConfig.get();
            if (c.enabled && c.explosionBoost) {
                explosionUntil = System.currentTimeMillis() + 4000;
            }
        } catch (Throwable ignored) { }
    }

    /** Bu varlığın çizimi şu an atlanmalı mı? */
    public static boolean shouldCull(Entity e, double x, double y, double z) {
        boolean exploding = explosionActive();
        if (!cullActive && !exploding) return false;
        double d = e.squaredDistanceTo(x, y, z);

        if (e instanceof TntEntity || e instanceof EndCrystalEntity) {
            // TNT/kristal: sadece patlama sırasında veya FPS çok düşükken, uzaktakiler
            return (exploding && d > EXPLOSION_CULL_SQ) || (cullActive && d > cullDistSq);
        }
        if (e instanceof ItemEntity || e instanceof ExperienceOrbEntity || e instanceof ExperienceBottleEntity) {
            double limit = exploding ? Math.min(cullDistSq, EXPLOSION_CULL_SQ) : cullDistSq;
            return d > limit;
        }
        return false;
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (failed) return;
            if (++ticks < 20) return; // 20 tick = 1 saniye
            ticks = 0;
            try {
                run(client);
            } catch (Throwable t) {
                failed = true; // hata olursa kendini kapat, oyunu çökertme
                cullActive = false;
                AutoBoostMod.LOGGER.error("AutoBoost devre dışı bırakıldı", t);
            }
        });
    }

    private static void run(MinecraftClient mc) {
        BoostConfig cfg = BoostConfig.get();
        int fps = mc.getCurrentFps();

        // Saniye başı değişim (HUD)
        fpsDelta = lastFps < 0 ? 0 : fps - lastFps;
        lastFps = fps;
        fpsNow = fps;

        if (!cfg.enabled || mc.world == null) {
            cullActive = false;
            return;
        }

        // Son 30 saniyenin en yüksek FPS'i -> otomatik hedef
        history[histPos] = fps;
        histPos = (histPos + 1) % history.length;
        if (histCount < history.length) histCount++;
        int peak = 0;
        for (int i = 0; i < histCount; i++) peak = Math.max(peak, history[i]);

        double target = cfg.autoMode ? Math.max(60, peak * 0.85) : cfg.targetFps;
        effectiveTarget = (int) target;

        if (cfg.entityCulling) {
            if (fps < target * 0.5) { cullActive = true; cullDistSq = 10 * 10; }
            else if (fps < target * 0.7) { cullActive = true; cullDistSq = 16 * 16; }
            else if (fps < target) { cullActive = true; cullDistSq = 28 * 28; }
            else if (fps > target * 1.2) { cullActive = false; }
        } else {
            cullActive = false;
        }

        Runtime rt = Runtime.getRuntime();
        long used = rt.totalMemory() - rt.freeMemory();
        double ratio = (double) used / rt.maxMemory();

        // Bellek çok doluysa (çökme riski) çizimi ekstra azalt
        if (cfg.memoryGuard && ratio > 0.95 && cfg.entityCulling) {
            cullActive = true;
            cullDistSq = Math.min(cullDistSq, 12 * 12);
        }

        if (cfg.memoryGuard) {
            long now = System.currentTimeMillis();
            if (ratio > 0.92 && now - lastGc > 120_000) {
                lastGc = now;
                Thread th = new Thread(System::gc, "AutoBoost-GC");
                th.setDaemon(true);
                th.start();
            }
        }
    }
}
