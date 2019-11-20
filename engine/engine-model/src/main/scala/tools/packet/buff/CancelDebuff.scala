package tools.packet.buff

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CancelDebuff(private var _mask: Long) extends PacketInput {
  def mask: Long = _mask

  override def opcode(): SendOpcode = SendOpcode.CANCEL_BUFF
}