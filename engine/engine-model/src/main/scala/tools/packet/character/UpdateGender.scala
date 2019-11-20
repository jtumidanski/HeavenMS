package tools.packet.character

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class UpdateGender(private var _gender: Int) extends PacketInput {
  def gender: Int = _gender

  override def opcode(): SendOpcode = SendOpcode.SET_GENDER
}