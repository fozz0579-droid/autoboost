package com.autoboost;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class BoostConfigScreen extends Screen {
    private static final int[] FPS_STEPS = {30, 45, 60, 90, 120, 144, 240, 360, 500, 1000};
    private static final int W = 150;
    private final Screen parent;

    public BoostConfigScreen(Screen parent) {
        super(Text.literal("AutoBoost Ayarları"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        BoostConfig c = BoostConfig.get();
        int xl = this.width / 2 - W - 5;
        int xr = this.width / 2 + 5;
        int y = Math.max(40, this.height / 6);

        toggle(xl, y, "Mod", () -> c.enabled, v -> c.enabled = v);
        toggle(xr, y, "Sınırsız FPS", () -> c.unlimitedFps, v -> c.unlimitedFps = v);
        toggle(xl, y + 24, "Otomatik mod", () -> c.autoMode, v -> c.autoMode = v);
        toggle(xr, y + 24, "Patlama koruması", () -> c.explosionBoost, v -> c.explosionBoost = v);
        toggle(xl, y + 48, "FPS göstergesi", () -> c.showFpsHud, v -> c.showFpsHud = v);
        toggle(xr, y + 48, "Eşya/TNT/XP kırpma", () -> c.entityCulling, v -> c.entityCulling = v);
        toggle(xl, y + 72, "Arka plan sınırı", () -> c.backgroundLimit, v -> c.backgroundLimit = v);
        toggle(xr, y + 72, "Bellek koruması", () -> c.memoryGuard, v -> c.memoryGuard = v);

        this.addDrawableChild(ButtonWidget.builder(fpsLabel(c), btn -> {
            int next = FPS_STEPS[0];
            for (int s : FPS_STEPS) {
                if (s > c.targetFps) { next = s; break; }
            }
            c.targetFps = next;
            btn.setMessage(fpsLabel(c));
            BoostConfig.save();
        }).dimensions(this.width / 2 - W - 5, y + 96, W * 2 + 10, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Bitti"), btn -> this.close())
                .dimensions(this.width / 2 - 100, y + 132, 200, 20).build());
    }

    private static Text fpsLabel(BoostConfig c) {
        return Text.literal("Hedef FPS (Otomatik mod kapalıyken): " + c.targetFps);
    }

    private static Text label(String name, boolean on) {
        return Text.literal(name + ": " + (on ? "Açık" : "Kapalı"));
    }

    private void toggle(int x, int y, String name, BooleanSupplier get, Consumer<Boolean> set) {
        this.addDrawableChild(ButtonWidget.builder(label(name, get.getAsBoolean()), btn -> {
            set.accept(!get.getAsBoolean());
            btn.setMessage(label(name, get.getAsBoolean()));
            BoostConfig.save();
        }).dimensions(x, y, W, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFFFF);
    }

    @Override
    public void close() {
        BoostConfig.save();
        if (this.client != null) this.client.setScreen(parent);
    }
}
