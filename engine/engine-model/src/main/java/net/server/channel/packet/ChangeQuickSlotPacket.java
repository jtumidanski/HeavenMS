package net.server.channel.packet;

import net.server.MaplePacket;

public record ChangeQuickSlotPacket(byte[] keyMap) implements MaplePacket {
}
