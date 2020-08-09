package net.server.channel.packet;

import net.server.MaplePacket;

public record InventoryMergePacket(Byte inventoryType) implements MaplePacket {
}
