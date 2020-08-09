package net.server.channel.packet;

import net.server.MaplePacket;

public record TransferNamePacket(Integer characterId, Integer birthday) implements MaplePacket {
}
