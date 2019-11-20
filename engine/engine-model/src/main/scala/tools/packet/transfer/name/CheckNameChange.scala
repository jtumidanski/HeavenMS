package tools.packet.transfer.name

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CheckNameChange(private var _availableName: String, private var _canUseName: Boolean) extends PacketInput {
  def availableName: String = _availableName

  def canUseName: Boolean = _canUseName

  override def opcode(): SendOpcode = SendOpcode.CASHSHOP_CHECK_NAME_CHANGE
}