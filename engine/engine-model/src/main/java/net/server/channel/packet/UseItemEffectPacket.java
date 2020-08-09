package net.server.channel.packet;

import net.server.MaplePacket;

public record UseItemEffectPacket(Integer itemId) implements MaplePacket {
}
