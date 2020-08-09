package net.server.channel.packet;

import net.server.MaplePacket;

public record UseItemUIPacket(Byte inventoryType, Short slot, Integer itemId) implements MaplePacket {
}
