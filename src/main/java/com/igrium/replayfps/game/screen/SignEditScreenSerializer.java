package com.igrium.replayfps.game.screen;

import com.igrium.replayfps.core.screen.ScreenSerializer;
import com.igrium.replayfps.core.screen.ScreenState;
import com.igrium.replayfps.game.mixin.AbstractSignEditScreenAccessor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;

public class SignEditScreenSerializer implements ScreenSerializer {
    public SignEditScreenSerializer.State readBuffer(RegistryByteBuf buf) {
        return SignEditScreenSerializer.State.CODEC.decode(buf);
    }

    public SignEditScreenSerializer.State serialize(Screen screen) {
        var accessor = (AbstractSignEditScreenAccessor) screen;
        var block = accessor.getBlockEntity();

        return new SignEditScreenSerializer.State(
                block.getPos(),
                Arrays.asList(accessor.getMessages().clone()),
                accessor.isFront(),
                accessor.getCurrentRow()
        );
    }

    public record State(BlockPos blockPos, List<String> messages, boolean front, int currentRow)
            implements ScreenState {
        static final PacketCodec<RegistryByteBuf, State> CODEC =
                PacketCodec.tuple(BlockPos.PACKET_CODEC, State::blockPos,
                        PacketCodecs.STRING.collect(PacketCodecs.toList()), State::messages,
                        PacketCodecs.BOOL, State::front,
                        PacketCodecs.INTEGER, State::currentRow,
                        State::new);

        public void writeBuffer(RegistryByteBuf buf) {
            CODEC.encode(buf, this);
        }

        public void apply(MinecraftClient client, Screen screen) {
            AbstractSignEditScreenAccessor accessor = (AbstractSignEditScreenAccessor) screen;
            String[] screenMessages = accessor.getMessages();
            for (int i = 0; i < screenMessages.length && i < messages.size(); i++)
                screenMessages[i] = messages.get(i);
            accessor.setCurrentRow(currentRow);
        }

        public AbstractSignEditScreen create(MinecraftClient client) {
            if (client.world == null) return null;
            var sign = client.world.getBlockEntity(blockPos, BlockEntityType.SIGN);
            if (sign.isPresent())
                return new SignEditScreen(sign.get(), front, client.shouldFilterText());
            var hangingSign = client.world.getBlockEntity(blockPos, BlockEntityType.HANGING_SIGN);
            if (hangingSign.isPresent())
                return new HangingSignEditScreen(hangingSign.get(), front, client.shouldFilterText());
            throw new IllegalStateException("No sign block entity found.");
        }

        public boolean hasChanged(Screen screen) {
            var accessor = (AbstractSignEditScreenAccessor) screen;
            var changed = !Arrays.asList(accessor.getMessages()).equals(messages) ||
                    front != accessor.isFront() ||
                    currentRow != accessor.getCurrentRow();
            if (changed)
                System.out.println("Sign has changed");
            return changed;
        }
    }
}
