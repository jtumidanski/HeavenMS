package net.server.login.packet;

import net.server.MaplePacket;

public record ServerStatusRequestPacket(Byte world) implements MaplePacket {
}
