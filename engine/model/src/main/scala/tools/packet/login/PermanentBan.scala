package tools.packet.login

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class PermanentBan(private var _reason: Byte) extends PacketInput {
  def reason: Byte = _reason

  override def opcode(): SendOpcode = SendOpcode.LOGIN_STATUS
}
