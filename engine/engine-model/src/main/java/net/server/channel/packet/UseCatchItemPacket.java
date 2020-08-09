package net.server.channel.packet;

import net.server.MaplePacket;

public record UseCatchItemPacket(Integer itemId, Integer monsterId) implements MaplePacket {
}
