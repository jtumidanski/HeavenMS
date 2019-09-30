package tools.packet.quest.info

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class QuestFailure(private var _type: Byte) extends PacketInput {
  def theType: Byte = _type

  override def opcode(): SendOpcode = SendOpcode.UPDATE_QUEST_INFO
}