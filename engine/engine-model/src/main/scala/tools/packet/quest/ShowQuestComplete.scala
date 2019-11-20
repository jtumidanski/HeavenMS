package tools.packet.quest

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowQuestComplete(private var _questId: Int) extends PacketInput {
  def questId: Int = _questId

  override def opcode(): SendOpcode = SendOpcode.QUEST_CLEAR
}