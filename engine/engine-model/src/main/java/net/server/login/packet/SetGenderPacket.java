package net.server.login.packet;

import net.server.MaplePacket;

public record SetGenderPacket(Byte confirmed, Byte gender) implements MaplePacket {
}
