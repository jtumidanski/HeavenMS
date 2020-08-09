package net.server.channel.packet;

import net.server.MaplePacket;

public record UseChairPacket(Integer itemId) implements MaplePacket {
}
