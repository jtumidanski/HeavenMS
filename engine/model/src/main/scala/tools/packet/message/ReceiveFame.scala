package tools.packet.message

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ReceiveFame(private var _mode: Int, private var _characterNameFrom: String) extends PacketInput {
  def mode: Int = _mode

  def characterNameFrom: String = _characterNameFrom

  override def opcode(): SendOpcode = SendOpcode.FAME_RESPONSE
}