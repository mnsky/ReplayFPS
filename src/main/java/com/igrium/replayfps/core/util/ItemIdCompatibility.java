package com.igrium.replayfps.core.util;

import com.replaymod.replaystudio.lib.viaversion.api.data.MappingData;
import com.replaymod.replaystudio.lib.viaversion.api.protocol.version.ProtocolVersion;
import com.replaymod.replaystudio.lib.viaversion.protocols.v1_20_2to1_20_3.Protocol1_20_2To1_20_3;
import com.replaymod.replaystudio.lib.viaversion.protocols.v1_20_3to1_20_5.Protocol1_20_3To1_20_5;
import com.replaymod.replaystudio.lib.viaversion.protocols.v1_20_5to1_21.Protocol1_20_5To1_21;
import com.replaymod.replaystudio.lib.viaversion.protocols.v1_20to1_20_2.Protocol1_20To1_20_2;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemIdCompatibility {
    private static final List<VersionAndMappings> ALL_MAPPINGS = Arrays.asList(
            new VersionAndMappings(ProtocolVersion.v1_20_2, Protocol1_20To1_20_2.MAPPINGS),
            new VersionAndMappings(ProtocolVersion.v1_20_3, Protocol1_20_2To1_20_3.MAPPINGS),
            new VersionAndMappings(ProtocolVersion.v1_20_5, Protocol1_20_3To1_20_5.MAPPINGS),
            new VersionAndMappings(ProtocolVersion.v1_21, Protocol1_20_5To1_21.MAPPINGS)
    );

    private static ProtocolVersion fromVersion = ProtocolVersion.unknown;
    private static List<MappingData> mappings;
    private static Map<Integer, Integer> cache;

    public static int getNewItemId(ProtocolVersion fromVersion, int oldId) {
        setFromVersion(fromVersion);
        int newId = cache.getOrDefault(oldId, -1);
        if (newId == -1) {
            newId = oldId;
            for (MappingData m : mappings)
                newId = m.getNewItemId(newId);
            cache.put(oldId, newId);
        }
        return newId;
    }

    private static void setFromVersion(ProtocolVersion version) {
        if (fromVersion.equals(version)) return;
        fromVersion = version;
        cache = new HashMap<>();
        mappings = ALL_MAPPINGS.stream()
                .filter(x -> x.toVersion.newerThan(fromVersion))
                .map(x -> x.mapping)
                .toList();
    }

    private record VersionAndMappings(ProtocolVersion toVersion, MappingData mapping) {}
}
