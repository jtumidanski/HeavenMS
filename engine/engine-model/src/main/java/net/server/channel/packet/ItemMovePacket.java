package net.server.channel.packet;

import net.server.MaplePacket;

public record ItemMovePacket(Byte inventoryType, Short source, Short action, Short quantity) implements MaplePacket {
}
