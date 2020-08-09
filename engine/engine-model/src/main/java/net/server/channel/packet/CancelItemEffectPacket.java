package net.server.channel.packet;

import net.server.MaplePacket;

public record CancelItemEffectPacket(Integer itemId) implements MaplePacket {
}
