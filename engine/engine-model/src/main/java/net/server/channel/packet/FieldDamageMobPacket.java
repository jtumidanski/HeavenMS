package net.server.channel.packet;

import net.server.MaplePacket;

public record FieldDamageMobPacket(Integer mobId, Integer damage) implements MaplePacket {
}
