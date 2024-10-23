package com.igrium.replayfps.game.screen;

import com.igrium.replayfps.core.screen.ScreenSerializer;
import com.igrium.replayfps.core.screen.ScreenState;
import com.igrium.replayfps.game.mixin.DeathScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;

public class DeathScreenSerializer implements ScreenSerializer {
    public DeathScreenSerializer.State readBuffer(RegistryByteBuf buf) {
        return DeathScreenSerializer.State.CODEC.decode(buf);
    }

    public DeathScreenSerializer.State serialize(Screen screen) {
        var accessor = (DeathScreenAccessor) screen;
        return new DeathScreenSerializer.State(
                accessor.getMessage().getString(),
                accessor.isHardcore(),
                accessor.getScoreText().getString()
        );
    }

    public record State(String message, boolean hardcore, String score)
            implements ScreenState {
        static final PacketCodec<RegistryByteBuf, State> CODEC =
                PacketCodec.tuple(PacketCodecs.STRING, State::message,
                        PacketCodecs.BOOL, State::hardcore,
                        PacketCodecs.STRING, State::score,
                        State::new);

        public void writeBuffer(RegistryByteBuf buf) {
            CODEC.encode(buf, this);
        }

        public void apply(MinecraftClient client, Screen screen) {
            ((DeathScreenAccessor) screen).setScoreText(Text.of(score));
        }

        public DeathScreen create(MinecraftClient client) {
            return new DeathScreen(Text.of(message), hardcore);
        }

        public boolean hasChanged(Screen screen) {
            return score.equals(((DeathScreenAccessor) screen).getScoreText().getString());
        }
    }
}
