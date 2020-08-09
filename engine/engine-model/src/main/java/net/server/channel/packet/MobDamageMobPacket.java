package net.server.channel.packet;

import net.server.MaplePacket;

public record MobDamageMobPacket(Integer from, Integer to, Boolean magic, Integer damage) implements MaplePacket {
}
