package net.server.channel.packet.family;

import net.server.MaplePacket;

public record FamilySeparatePacket(Boolean available, Integer characterId) implements MaplePacket {
}
