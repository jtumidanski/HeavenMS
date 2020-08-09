package net.server.channel.packet.pet;

import net.server.MaplePacket;

public record PetExcludeItemsPacket(int petId, byte amount, int[] itemIds) implements MaplePacket {
}
