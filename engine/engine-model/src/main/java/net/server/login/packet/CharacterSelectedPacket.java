package net.server.login.packet;

import net.server.MaplePacket;

public record CharacterSelectedPacket(Integer characterId, String macs, String hwid) implements MaplePacket {
}
