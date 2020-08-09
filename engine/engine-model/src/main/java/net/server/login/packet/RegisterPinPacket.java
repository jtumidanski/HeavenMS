package net.server.login.packet;

import net.server.MaplePacket;

public record RegisterPinPacket(Byte byte1, String pin) implements MaplePacket {
}
