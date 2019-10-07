package tools.packet.buff

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CancelForeignSlowDebuff(private var _characterId: Int) extends PacketInput {
  def characterId: Int = _characterId

  override def opcode(): SendOpcode = SendOpcode.CANCEL_FOREIGN_BUFF
}