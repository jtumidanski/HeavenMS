package tools.packet.statusinfo

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CompleteQuest(private var _questId: Short, private var _time: Long) extends PacketInput {
  def questId: Short = _questId

  def time: Long = _time

  override def opcode(): SendOpcode = SendOpcode.SHOW_STATUS_INFO
}