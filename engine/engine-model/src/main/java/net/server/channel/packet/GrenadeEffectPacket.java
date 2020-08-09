package net.server.channel.packet;

import net.server.MaplePacket;

public record GrenadeEffectPacket(Integer x, Integer y, Integer keyDown, Integer skillId) implements MaplePacket {
}
