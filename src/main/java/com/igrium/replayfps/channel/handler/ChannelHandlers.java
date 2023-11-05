package com.igrium.replayfps.channel.handler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.igrium.replayfps.channel.type.ChannelType;
import com.igrium.replayfps.channel.type.ChannelTypes;
import com.igrium.replayfps.channel.type.PlaceholderChannel;
import com.igrium.replayfps.recording.ClientCaptureContext;

import net.minecraft.util.Identifier;

public class ChannelHandlers {
    
    public static final BiMap<Identifier, ChannelHandler<?>> REGISTRY = HashBiMap.create();

    public static final ChannelHandler<?> DUMMY = register(new DummyChannelHandler(), new Identifier("replayfps:dummy"));
    public static final ViewMatrixChannelHandler VIEW_MATRIX = register(new ViewMatrixChannelHandler(), new Identifier("replayfps:viewmatrix"));

    public static class PlaceholderChannelHandler implements ChannelHandler<Object> {
        private final ChannelType<Object> type;

        public PlaceholderChannelHandler(int size) {
            this.type = new PlaceholderChannel(size);
        }

        @Override
        public ChannelType<Object> getChannelType() {
            return type;
        }

        @Override
        public Object capture(ClientCaptureContext context) {
            return null;
        }

        @Override
        public void apply(Object val) {
        }
    }

    /**
     * Register a channel handler.
     * @param <T> Channel handler type.
     * @param handler The channel handler.
     * @param id ID to register with.
     * @return <code>handler</code>
     */
    public static <T extends ChannelHandler<?>> T register(T handler, Identifier id) {
        REGISTRY.put(id, handler);
        return handler;
    }

    private static class DummyChannelHandler implements ChannelHandler<Short> {

        @Override
        public ChannelType<Short> getChannelType() {
            return ChannelTypes.SHORT;
        } 

        @Override
        public Short capture(ClientCaptureContext context) {
            return 0xFBF;
        }

        @Override
        public void apply(Short val) {
        }
        
    }
}
