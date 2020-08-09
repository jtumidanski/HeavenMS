package net.server.channel.packet;

import net.server.MaplePacket;
import net.server.SkillMacro;

public record SkillMacroPacket(SkillMacro[] macros) implements MaplePacket {
}
