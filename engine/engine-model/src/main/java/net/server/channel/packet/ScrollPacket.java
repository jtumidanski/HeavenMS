package net.server.channel.packet;

import net.server.MaplePacket;

public record ScrollPacket(Short slot, Short destination, Byte whiteScroll) implements MaplePacket {
}
