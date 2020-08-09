package net.server.channel.packet;

import net.server.MaplePacket;

public record SummonDamagePacket(int objectId, byte direction, int numAttacked, int[] monsterObjectId,
                                 int[] damage) implements MaplePacket {
}
