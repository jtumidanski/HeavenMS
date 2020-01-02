package tools.packet.buff

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CancelForeignAbnormalStatus(private var _characterId: Int, private var _mask: Long) extends PacketInput {
  def characterId: Int = _characterId

  def mask: Long = _mask

  override def opcode(): SendOpcode = SendOpcode.CANCEL_FOREIGN_BUFF
}