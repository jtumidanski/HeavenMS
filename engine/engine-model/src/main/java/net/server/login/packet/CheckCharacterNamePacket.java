package net.server.login.packet;

import net.server.MaplePacket;

public record CheckCharacterNamePacket(String name) implements MaplePacket {
}
