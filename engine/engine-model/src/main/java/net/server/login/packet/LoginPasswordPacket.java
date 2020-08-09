package net.server.login.packet;

import net.server.MaplePacket;

public record LoginPasswordPacket(String login, String password, byte[] hwid) implements MaplePacket {
}
