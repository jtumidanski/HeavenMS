package net.server.channel.packet;

import net.server.MaplePacket;

public record BeholderPacket(Integer objectId, Integer skillId) implements MaplePacket {
}
