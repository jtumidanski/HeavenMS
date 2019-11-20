package net.server.channel.packet

import net.server.{MaplePacket, SkillMacro}

class SkillMacroPacket( private var _macros: Array[SkillMacro]) extends MaplePacket {
     def macros: Array[SkillMacro] = _macros
}
