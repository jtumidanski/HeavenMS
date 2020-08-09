package net.server.channel.packet.family;

import net.server.MaplePacket;

public record FamilyUsePacket(Integer entitlementId, String characterName) implements MaplePacket {
}
