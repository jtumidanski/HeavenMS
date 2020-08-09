package net.server.channel.packet.family;

import net.server.MaplePacket;

public record FamilySummonResponsePacket(String familyName, Boolean accept) implements MaplePacket {
}
