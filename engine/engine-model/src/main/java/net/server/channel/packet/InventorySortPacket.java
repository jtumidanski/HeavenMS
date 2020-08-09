package net.server.channel.packet;

import net.server.MaplePacket;

public record InventorySortPacket(Byte inventoryType) implements MaplePacket {
}
