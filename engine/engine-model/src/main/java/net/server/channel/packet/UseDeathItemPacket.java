package net.server.channel.packet;

import net.server.MaplePacket;

public record UseDeathItemPacket(Integer itemId) implements MaplePacket {
}
