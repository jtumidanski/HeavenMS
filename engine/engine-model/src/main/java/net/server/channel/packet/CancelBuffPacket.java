package net.server.channel.packet;

import net.server.MaplePacket;

public record CancelBuffPacket(Integer sourceId) implements MaplePacket {
}
