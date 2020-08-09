package net.server.login.packet;

import net.server.MaplePacket;

public record ViewAllCharactersSelectedPacket(Integer characterId, String macs, String hwid) implements MaplePacket {
}
