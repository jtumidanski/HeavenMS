package net.server.channel.packet.family;

import net.server.MaplePacket;

public record FamilyAddPacket(String toAdd) implements MaplePacket {
}
