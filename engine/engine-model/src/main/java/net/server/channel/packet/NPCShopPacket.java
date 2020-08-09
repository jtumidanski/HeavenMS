package net.server.channel.packet;

import net.server.MaplePacket;

public record NPCShopPacket(Byte mode, Short slot, Integer itemId, Short quantity) implements MaplePacket {
}
