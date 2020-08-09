package net.server.channel.packet;

import net.server.MaplePacket;

public record SkillEffectPacket(Integer skillId, Integer level, Byte flags, Integer speed,
                                Byte aids) implements MaplePacket {
}
