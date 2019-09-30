package tools.packet.quest.info

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class UpdateQuestInfo(private var _questId: Short, private var _npcId: Int) extends PacketInput {
  def questId: Short = _questId

  def npcId: Int = _npcId

  override def opcode(): SendOpcode = SendOpcode.UPDATE_QUEST_INFO
}