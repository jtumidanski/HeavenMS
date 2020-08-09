package net.server.channel.packet.pet;

import net.server.MaplePacket;

public record PetLootPacket(Integer petIndex, Integer objectId) implements MaplePacket {
}
