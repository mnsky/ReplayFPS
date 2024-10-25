package com.igrium.replayfps.game.mixin;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSignEditScreen.class)
public interface AbstractSignEditScreenAccessor {
    @Accessor("blockEntity")
    SignBlockEntity getBlockEntity();

    @Accessor("messages")
    String[] getMessages();

    @Accessor("front")
    boolean isFront();

    @Accessor("currentRow")
    int getCurrentRow();

    @Accessor("currentRow")
    void setCurrentRow(int currentRow);
}
