package com.igrium.replayfps.core.screen;

import com.igrium.replayfps.ReplayFPS;
import com.igrium.replayfps.core.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PlaybackScreenManager {
    public static final Identifier MOUSE_TEXTURE = Identifier.of("replayfps", "textures/cursor/cursor_mc.png");

    private final MinecraftClient client;

    // Mouse position relative to center of the screen.
    private float mouseX;
    private float mouseY;

    public PlaybackScreenManager(MinecraftClient client) {
        this.client = client;
    }

    @Nullable
    private Screen screen;

    public void setMouseX(float mouseX) {
        this.mouseX = mouseX;
    }

    public void setMouseY(float mouseY) {
        this.mouseY = mouseY;
    }

    public final Optional<Screen> getScreen() {
        return Optional.ofNullable(screen);
    }

    public void setScreen(@Nullable Screen newScreen) {
        if (screen != null) {
            screen.removed();
            prevSizeX = -1;
            prevSizeY = -1;
        }
        if (newScreen != null)
            newScreen.onDisplayed();
        screen = newScreen;
    }

    public final void clearScreen() {
        setScreen(null);
    }

    private int prevSizeX = -1;
    private int prevSizeY = -1;

    public void render(DrawContext drawContext, float tickDelta) {
        if (screen == null || !ReplayFPS.getConfig().shouldDrawScreens()) return;

        // Don't draw over the game menu.
        if (client.currentScreen instanceof GameMenuScreen) return;

        int sizeX = drawContext.getScaledWindowWidth();
        int sizeY = drawContext.getScaledWindowHeight();

        // Mouse is recorded relative to center of screen.
        float mouseX = this.mouseX + sizeX / 2.0f;
        float mouseY = this.mouseY + sizeY / 2.0f;

        if (prevSizeX != sizeX || prevSizeY != sizeY) {
            screen.init(client, sizeX, sizeY);
            prevSizeX = sizeX;
            prevSizeY = sizeY;
        }

        screen.render(drawContext, (int) mouseX, (int) mouseY, tickDelta);

        drawMouse(drawContext, mouseX, mouseY);
    }

    private void drawMouse(DrawContext context, float x, float y) {
        float x2 = x + 8;
        float y2 = y + 8;

        RenderUtils.drawTexturedQuad(MOUSE_TEXTURE,
                x, x2, y, y2, 64,
                0, 1, 0, 1, context.getMatrices());
    }

    public void tick() {
        if (screen != null) screen.tick();
    }

    public MinecraftClient getClient() {
        return client;
    }

    public void openScreen(ScreenState screenState) {
        Screen screen = screenState.create(client);
        setScreen(screen);
    }
}
