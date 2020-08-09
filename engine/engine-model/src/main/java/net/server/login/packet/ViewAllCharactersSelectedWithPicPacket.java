package net.server.login.packet;

import net.server.MaplePacket;

public record ViewAllCharactersSelectedWithPicPacket(String pic, Integer characterId, String macs,
                                                     String hwid) implements MaplePacket {
}
