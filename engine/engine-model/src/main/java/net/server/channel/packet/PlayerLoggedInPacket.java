package net.server.channel.packet;

import net.server.MaplePacket;

public record PlayerLoggedInPacket(Integer characterId) implements MaplePacket {
}
