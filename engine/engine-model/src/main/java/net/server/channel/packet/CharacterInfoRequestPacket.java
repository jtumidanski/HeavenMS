package net.server.channel.packet;

import net.server.MaplePacket;

public record CharacterInfoRequestPacket(Integer characterId) implements MaplePacket {
}
