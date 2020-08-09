package net.server.channel.packet;

import net.server.MaplePacket;

public record GiveFamePacket(Integer characterId, Integer mode) implements MaplePacket {
}
