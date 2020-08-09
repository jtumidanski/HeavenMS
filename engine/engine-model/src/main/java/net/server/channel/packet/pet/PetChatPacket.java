package net.server.channel.packet.pet;

import net.server.MaplePacket;

public record PetChatPacket(Integer petId, Integer act, String text) implements MaplePacket {
}
