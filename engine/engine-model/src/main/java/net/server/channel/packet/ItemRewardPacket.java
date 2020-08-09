package net.server.channel.packet;

import net.server.MaplePacket;

public record ItemRewardPacket(Byte slot, Integer itemId) implements MaplePacket {
}
