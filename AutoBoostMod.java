package com.autoboost;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AutoBoostMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("autoboost");

    @Override
    public void onInitializeClient() {
        BoostConfig.load();
        Optimizer.init();

        // Ekranda FPS + saniye başı değişim göstergesi
        try {
            HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
                    Identifier.of("autoboost", "fps_hud"), (ctx, tick) -> {
                        try {
                            BoostConfig c = BoostConfig.get();
                            MinecraftClient mc = MinecraftClient.getInstance();
                            if (!c.enabled || !c.showFpsHud || mc.options.hudHidden) return;
                            int d = Optimizer.fpsDelta;
                            String txt = "FPS " + Optimizer.fpsNow + " (" + (d >= 0 ? "+" : "") + d + "/sn)";
                            int color = d >= 0 ? 0xFF55FF55 : 0xFFFF5555;
                            ctx.drawTextWithShadow(mc.textRenderer, txt, 4, 4, color);
                        } catch (Throwable ignored) { }
                    });
        } catch (Throwable t) {
            LOGGER.error("FPS göstergesi eklenemedi", t);
        }

        // "Çoklu Oyuncu" butonunun altına ayar butonu ekle
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (!(screen instanceof TitleScreen)) return;
            try {
                List<ClickableWidget> buttons = Screens.getButtons(screen);
                ClickableWidget multiplayer = null;
                for (ClickableWidget b : buttons) {
                    if (b.getMessage().getContent() instanceof TranslatableTextContent t
                            && t.getKey().equals("menu.multiplayer")) {
                        multiplayer = b;
                        break;
                    }
                }
                if (multiplayer == null) return;

                int gap = 24;
                int baseY = multiplayer.getY();
                for (ClickableWidget b : new ArrayList<>(buttons)) {
                    if (b != multiplayer && b.getY() > baseY) b.setY(b.getY() + gap);
                }

                ButtonWidget btn = ButtonWidget.builder(Text.literal("AutoBoost Ayarları"),
                                x -> client.setScreen(new BoostConfigScreen(screen)))
                        .dimensions(multiplayer.getX(), baseY + gap, multiplayer.getWidth(), 20)
                        .build();
                buttons.add(btn);
            } catch (Throwable t) {
                LOGGER.error("Ayar butonu eklenemedi", t);
            }
        });
    }
}
