package net.server.channel.packet;

import net.server.MaplePacket;

public record ChangeMapPacket(Boolean cashShop, Byte fromDying, Integer targetId, String startWarp,
                              Boolean wheel) implements MaplePacket {
}
