package com.igrium.replayfps.core.playback;

import com.igrium.replayfps.core.channel.ChannelHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChannelValueCache {
    private final Map<ChannelHandler<?>, Object> map = new HashMap<>();
    private final Map<ChannelHandler<?>, Object> unmodifiable = Collections.unmodifiableMap(map);

    public <T> void put(ChannelHandler<T> channel, T value) {
        map.put(channel, value);
    }

    public Map<ChannelHandler<?>, Object> map() {
        return unmodifiable;
    }

    public void clear() {
        map.clear();
    }
}
