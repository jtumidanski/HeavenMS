package net.server.channel.packet.pet;

import net.server.MaplePacket;

public record PetAutoPotPacket(Short slot, Integer itemId) implements MaplePacket {
}
