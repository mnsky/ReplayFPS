package com.igrium.replayfps.core.channel.type;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

/**
 * A channel type designed to be used as a placeholder when a channel type was not found.
 */
public class PlaceholderChannel implements ChannelType<Object> {
    private final int size;
    private final byte[] buffer;

    public PlaceholderChannel(int size) {
        this.size = size;
        this.buffer = new byte[size];
    }

    @Override
    public Class<Object> getType() {
        return Object.class;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Object read(DataInput in) throws IOException {
        in.readFully(buffer);
        return null;
    }

    @Override
    public void write(DataOutput out, Object val) throws IOException {
        Arrays.fill(buffer, (byte) 0);
        out.write(buffer);
    }

    @Override
    public Object defaultValue() {
        return null;
    }
}
