package com.igrium.replayfps.game.mixin;

import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DeathScreen.class)
public interface DeathScreenAccessor {

    @Accessor("message")
    public Text getMessage();

    @Accessor("isHardcore")
    public boolean isHardcore();

    @Accessor("scoreText")
    public Text getScoreText();

    @Accessor("scoreText")
    public void setScoreText(Text scoreText);
}
