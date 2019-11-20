package tools.packet.transfer.name

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class NameChangeCancel(private var _success: Boolean) extends PacketInput {
  def success: Boolean = _success

  override def opcode(): SendOpcode = SendOpcode.CANCEL_NAME_CHANGE_RESULT
}