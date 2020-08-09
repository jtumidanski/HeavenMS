package net.server.login.packet;

import net.server.MaplePacket;

public record ViewAllCharactersRegisterPicPacket(Integer characterId, String mac, String hwid,
                                                 String pic) implements MaplePacket {
}
