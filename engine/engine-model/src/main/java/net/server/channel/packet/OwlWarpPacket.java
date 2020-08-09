package net.server.channel.packet;

import net.server.MaplePacket;

public record OwlWarpPacket(Integer ownerId, Integer mapId) implements MaplePacket {
}
