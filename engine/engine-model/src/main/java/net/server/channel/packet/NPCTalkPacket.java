package net.server.channel.packet;

import net.server.MaplePacket;

public record NPCTalkPacket(Integer objectId) implements MaplePacket {
}
