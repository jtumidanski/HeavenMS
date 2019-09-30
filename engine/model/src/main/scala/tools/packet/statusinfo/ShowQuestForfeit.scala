package tools.packet.statusinfo

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowQuestForfeit(private var _questId: Short) extends PacketInput {
  def questId: Short = _questId

  override def opcode(): SendOpcode = SendOpcode.SHOW_STATUS_INFO
}