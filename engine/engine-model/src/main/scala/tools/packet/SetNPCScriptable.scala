package tools.packet

import net.opcodes.SendOpcode
import tools.Pair

class SetNPCScriptable(private var _descriptions: java.util.Set[Pair[java.lang.Integer, String]]) extends PacketInput {
  def descriptions: java.util.Set[Pair[java.lang.Integer, String]] = _descriptions

  override def opcode(): SendOpcode = SendOpcode.SET_NPC_SCRIPTABLE
}