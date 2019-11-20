package tools.packet

import net.opcodes.SendOpcode

class AfterLoginError(private var _reason: Int) extends PacketInput {
  def reason: Int = _reason

  override def opcode(): SendOpcode = SendOpcode.SELECT_CHARACTER_BY_VAC
}