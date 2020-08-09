package net.server.channel.packet;

import net.server.MaplePacket;

public record UseItemPacket(Short slot, Integer itemId) implements MaplePacket {
}
