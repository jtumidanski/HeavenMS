package net.server.channel.packet;

import net.server.MaplePacket;

public record TouchReactorPacket(Integer objectId, Boolean isTouching) implements MaplePacket {
}
