package net.server.channel.packet;

import net.server.MaplePacket;

public record UseMountFoodPacket(Short position, Integer itemId) implements MaplePacket {
}
