package tools.packet.transfer.name

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class NameChangeError(private var _error: Int) extends PacketInput {
  def error: Int = _error

  override def opcode(): SendOpcode = SendOpcode.CASH_SHOP_CHECK_NAME_CHANGE_POSSIBLE_RESULT
}