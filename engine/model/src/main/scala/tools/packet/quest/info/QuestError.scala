package tools.packet.quest.info

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class QuestError(private var _questId: Short) extends PacketInput {
  def questId: Short = _questId

  override def opcode(): SendOpcode = SendOpcode.UPDATE_QUEST_INFO
}