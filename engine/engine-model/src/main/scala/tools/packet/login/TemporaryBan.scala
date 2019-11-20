package tools.packet.login

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class TemporaryBan(private var _timestampUntil: Long, private var _reason: Byte) extends PacketInput {
  def timestampUntil: Long = _timestampUntil

  def reason: Byte = _reason

  override def opcode(): SendOpcode = SendOpcode.LOGIN_STATUS
}
