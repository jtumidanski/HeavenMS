package net.server.channel.packet;

import net.server.MaplePacket;

public record UseSolomonPacket(Short slot, Integer itemId) implements MaplePacket {
}
