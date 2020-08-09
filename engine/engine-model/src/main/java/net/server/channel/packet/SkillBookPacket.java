package net.server.channel.packet;

import net.server.MaplePacket;

public record SkillBookPacket(Short slot, Integer itemId) implements MaplePacket {
}
