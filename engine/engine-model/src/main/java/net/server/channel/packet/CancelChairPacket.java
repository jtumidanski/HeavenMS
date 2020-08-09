package net.server.channel.packet;

import net.server.MaplePacket;

public record CancelChairPacket(Integer itemId) implements MaplePacket {
}
