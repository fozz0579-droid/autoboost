package com.autoboost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class BoostConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("autoboost.json");
    private static BoostConfig instance = new BoostConfig();

    public boolean enabled = true;          // Ana şalter
    public boolean autoMode = true;         // Hedef FPS'i otomatik belirle
    public boolean unlimitedFps = true;     // FPS sınırını kaldır (1000 FPS için)
    public boolean explosionBoost = true;   // Patlamada (TNT/kristal) otomatik FPS koruması
    public boolean showFpsHud = true;       // Ekranda FPS ve saniye başı değişim
    public boolean backgroundLimit = true;  // Pencere odakta değilken FPS sınırı
    public int backgroundFps = 15;
    public boolean entityCulling = true;    // FPS düşünce uzak eşya/XP/TNT/kristal çizimini atla
    public int targetFps = 60;
    public boolean memoryGuard = true;      // Bellek dolunca arka planda temizlik

    public static BoostConfig get() { return instance; }

    public static void load() {
        try {
            if (Files.exists(PATH)) {
                BoostConfig c = GSON.fromJson(Files.readString(PATH), BoostConfig.class);
                if (c != null) instance = c;
            }
            instance.targetFps = Math.max(20, Math.min(1000, instance.targetFps));
            instance.backgroundFps = Math.max(5, Math.min(60, instance.backgroundFps));
        } catch (Throwable t) {
            instance = new BoostConfig(); // bozuk dosya oyunu çökertmesin
        }
        save();
    }

    public static void save() {
        try {
            Files.writeString(PATH, GSON.toJson(instance));
        } catch (Throwable ignored) { }
    }
}
