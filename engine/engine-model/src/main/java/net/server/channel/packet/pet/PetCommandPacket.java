package net.server.channel.packet.pet;

import net.server.MaplePacket;

public record PetCommandPacket(int petId, byte command) implements MaplePacket {
}
