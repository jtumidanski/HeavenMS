package net.server.channel.packet.family;

import net.server.MaplePacket;

public record AcceptFamilyPacket(Integer inviterId, Boolean accept) implements MaplePacket {
}
