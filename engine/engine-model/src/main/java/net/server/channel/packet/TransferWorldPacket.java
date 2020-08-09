package net.server.channel.packet;

import net.server.MaplePacket;

public record TransferWorldPacket(Integer characterId, Integer birthday) implements MaplePacket {
}
