package net.server.login.packet;

import net.server.MaplePacket;

public record CharacterListRequestPacket(int world, int channel) implements MaplePacket {
}
