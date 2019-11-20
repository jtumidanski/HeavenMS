package tools.packet.report

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SendPolice(private var _text: String) extends PacketInput {
  def text: String = _text

  override def opcode(): SendOpcode = SendOpcode.DATA_CRC_CHECK_FAILED
}