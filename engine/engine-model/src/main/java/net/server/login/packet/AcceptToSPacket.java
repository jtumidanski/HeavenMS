package net.server.login.packet;

import net.server.MaplePacket;

public record AcceptToSPacket(byte[] bytes) implements MaplePacket {
}
