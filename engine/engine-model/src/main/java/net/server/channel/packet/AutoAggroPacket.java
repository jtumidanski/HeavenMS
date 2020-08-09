package net.server.channel.packet;

import net.server.MaplePacket;

public record AutoAggroPacket(Integer objectId) implements MaplePacket {
}
