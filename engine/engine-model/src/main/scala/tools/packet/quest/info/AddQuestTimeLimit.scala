package tools.packet.quest.info

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class AddQuestTimeLimit(private var _questId: Short, private var _time: Int) extends PacketInput {
  def questId: Short = _questId

  def time: Int = _time

  override def opcode(): SendOpcode = SendOpcode.UPDATE_QUEST_INFO
}