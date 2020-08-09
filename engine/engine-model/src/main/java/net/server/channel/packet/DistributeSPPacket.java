package net.server.channel.packet;

import net.server.MaplePacket;

public record DistributeSPPacket(Integer skillId) implements MaplePacket {
}
