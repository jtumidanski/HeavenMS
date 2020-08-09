package net.server.channel.packet.family;

import net.server.MaplePacket;

public record OpenFamilyPedigreePacket(String characterName) implements MaplePacket {
}
