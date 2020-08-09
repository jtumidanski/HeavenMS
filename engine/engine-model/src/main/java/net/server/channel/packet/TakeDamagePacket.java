package net.server.channel.packet;

import net.server.MaplePacket;

public record TakeDamagePacket(Byte damageFrom, Byte element, Integer damage, Integer monsterIdFrom, Integer objectId,
                               Byte direction) implements MaplePacket {
}
