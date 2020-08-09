package net.server.channel.packet;

import net.server.MaplePacket;

public record MobDamageMobFriendlyPacket(Integer attacker, Integer damage) implements MaplePacket {
}
