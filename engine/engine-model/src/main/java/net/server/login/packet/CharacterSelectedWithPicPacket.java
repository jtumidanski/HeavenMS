package net.server.login.packet;

import net.server.MaplePacket;

public record CharacterSelectedWithPicPacket(String pic, Integer characterId, String macs,
                                             String hwid) implements MaplePacket {
}
