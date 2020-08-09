package net.server.login.packet;

import net.server.MaplePacket;

public record RegisterPicPacket(Integer characterId, String macs, String hwid, String pic) implements MaplePacket {
}
