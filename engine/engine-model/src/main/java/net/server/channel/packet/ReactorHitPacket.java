package net.server.channel.packet;

import net.server.MaplePacket;

public record ReactorHitPacket(Integer objectId, Integer characterPosition, Short stance,
                               Integer skillId) implements MaplePacket {
}
