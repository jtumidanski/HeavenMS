package net.server.packet;

import net.server.MaplePacket;

public record CustomPacket(byte[] bytes) implements MaplePacket {
}
