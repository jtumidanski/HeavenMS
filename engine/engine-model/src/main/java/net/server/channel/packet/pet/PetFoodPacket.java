package net.server.channel.packet.pet;

import net.server.MaplePacket;

public record PetFoodPacket(Integer timestamp, Short position, Integer itemId) implements MaplePacket {
}
