package net.server.login.packet;

import net.server.MaplePacket;

public record DeleteCharacterPacket(String pic, Integer characterId) implements MaplePacket {
}
