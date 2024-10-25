package com.igrium.replayfps.core.channel.type;

import com.igrium.replayfps.core.channel.type.NumberChannel.*;

public class ChannelTypes {
    public static final ByteChannel BYTE = new ByteChannel();
    public static final ShortChannel SHORT = new ShortChannel();
    public static final IntegerChannel INTEGER = new IntegerChannel();
    public static final LongChannel LONG = new LongChannel();
    public static final FloatChannel FLOAT = new FloatChannel();
    public static final DoubleChannel DOUBLE = new DoubleChannel();
    public static final UnsignedShortChannel UNSIGNED_SHORT = new UnsignedShortChannel();
    public static final UnsignedByteChannel UNSIGNED_BYTE = new UnsignedByteChannel();

    public static final Vector2fChannelType VECTOR2F = new Vector2fChannelType();

    public static final Vec3dChannelType VEC3D = new Vec3dChannelType();
}
