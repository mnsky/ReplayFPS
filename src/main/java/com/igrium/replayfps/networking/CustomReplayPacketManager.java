package com.igrium.replayfps.networking;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.igrium.replayfps.mixin.ClientConnectionAccessor;
import com.igrium.replayfps.util.PlaybackUtils;
import com.mojang.logging.LogUtils;

import io.netty.channel.Channel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

/**
 * Handles the injection of custom packets into a replay recording. This allows
 * arbitrary client instructions to be inserted into the replay without the need
 * for a dedicated client-cap channel.
 */
public class CustomReplayPacketManager {

    /**
     * A prefix added to the namespace of all replay packets, forwarding them to
     * this special set of handlers.
     */
    public static final String PREFIX = "rp_";

    public static interface ReplayPacketConsumer {
        public void onPacket(MinecraftClient client, PacketByteBuf packet, PlayerEntity localPlayer) throws Exception;

        /**
         * Don't apply this packet until we're in first-person. If <code>true</code>,
         * will delay until we enter first-person mode, at which time all queued packets
         * will play in order.
         */
        default boolean waitForFirstPerson() {
            return false;
        }
    }

    protected static record CachedValue(FakePacketHandler<?> handler, Object val) {};

    protected final Map<Identifier, ReplayPacketConsumer> listeners = new HashMap<>();
    protected final Queue<CachedValue> queue = new ConcurrentLinkedDeque<>();

    /**
     * Called whenever a custom packet of any kind is recieved on the client.
     * 
     * @param channel The packet channel.
     * @param payload The packet's payload.
     * @return If this packet should be "consumed". If <code>true</code> no other
     *         recievers (including the registered one) will recieve the packet.
     */
    public boolean onPacketReceived(Identifier channel, PacketByteBuf payload) {
        if (!channel.getNamespace().startsWith(PREFIX)) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity localPlayer = PlaybackUtils.getCurrentPlaybackPlayer();
        if (localPlayer == null) {
            return true;
        }

        String namespace = channel.getNamespace().substring(PREFIX.length());
        Identifier finalID = new Identifier(namespace, channel.getPath());

        ReplayPacketConsumer receiver = listeners.get(finalID);
        if (receiver == null) {
            LogUtils.getLogger().warn("Unknown replay packet channel: " + finalID);
            return true;
        }
        
        if (receiver instanceof FakePacketHandler handler) {
            boolean shouldRun = localPlayer.equals(client.cameraEntity) || !handler.waitForFirstPerson();

            if (shouldRun) {
                handler.onPacket(client, payload, localPlayer);
            } else {
                Object val = handler.parse(payload);
                queue.add(new CachedValue(handler, val));
            }

        } else {
            try {
                receiver.onPacket(client, payload, localPlayer);
            } catch (Exception e) {
                LogUtils.getLogger().error("Error parsing custom replay packet.", e);
            }
        }

        return true;
    }


    public void clearQueue() {
        queue.clear();
    }

    public void flushQueue(MinecraftClient client, PlayerEntity localPlayer) {
        MinecraftClient.getInstance().execute(() -> {
            CachedValue cached;
            while ((cached = queue.poll()) != null) {
                cached.handler.castAndApply(cached.val, client, localPlayer);
            }
        });
    }

    public void registerReceiver(Identifier id, ReplayPacketConsumer receiver) {
        listeners.put(id, receiver);
    }

    /**
     * Send a "fake" packet that will be "received" during replay playback.
     * @param id Packet channel.
     * @param packet Packet data.
     */
    public static void sendReplayPacket(Identifier id, PacketByteBuf payload) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler netHandler = client.getNetworkHandler();
        if (netHandler == null) {
            throw new IllegalStateException("Replay packets can only be sent while a game is active.");
        }
        
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(new Identifier(PREFIX + id.getNamespace(), id.getPath()), payload);
        sendFakePacket(packet);
    }

    /**
     * Trick the client into thinking a packet has been sent from the server. The
     * packet is injected into the netty pipeline so it will be captured by the
     * Replay Mod.
     */
    public static void sendFakePacket(Packet<?> packet) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler netHandler = client.getNetworkHandler();
        if (netHandler == null) {
            throw new IllegalStateException("Replay packets can only be sent while a game is active.");
        }

        if (PlaybackUtils.isPlayingReplay()) {
            LogUtils.getLogger().warn("Tried to send a fake packet from the replay editor.");
            return;
        }

        Channel channel = ((ClientConnectionAccessor) netHandler.getConnection()).replayfps$getChannel();
        if (channel.eventLoop().inEventLoop()) {
            sendFakePacketInternal(channel, packet);
        } else {
            channel.eventLoop().execute(() -> sendFakePacketInternal(channel, packet));
        }
    }

    private static void sendFakePacketInternal(Channel channel, Packet<?> packet) {
        channel.pipeline().fireChannelRead(packet);
    }

    // /**
    //  * Returns the current network state of the channel.
    //  */
    // private static NetworkState getState(Channel channel) {
    // 	return channel.attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get();
    // }

}
