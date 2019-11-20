package tools.packet.quest.info

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class QuestFinish(private var _questId: Short, private var _npcId: Int, private var _nextQuestId: Short) extends PacketInput {
  def questId: Short = _questId

  def npcId: Int = _npcId

  def nextQuestId: Short = _nextQuestId

  override def opcode(): SendOpcode = SendOpcode.UPDATE_QUEST_INFO
}