package net.server.channel.packet;

import net.server.MaplePacket;

public record DamageSummonPacket(Integer objectId, Integer damage, Integer monsterIdFrom) implements MaplePacket {
}
