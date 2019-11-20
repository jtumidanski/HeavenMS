package tools.packet.ui

import net.opcodes.SendOpcode
import net.server.SkillMacro
import tools.packet.PacketInput

class GetMacros(private var _macros: Array[SkillMacro]) extends PacketInput {
  def macros: Array[SkillMacro] = _macros

  override def opcode(): SendOpcode = SendOpcode.MACRO_SYS_DATA_INIT
}