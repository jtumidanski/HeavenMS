package tools.packet.message

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class NotifyJobAdvance(private var _type: Int, private var _job: Int, private var _characterName: String) extends PacketInput {
  def theType: Int = _type

  def job: Int = _job

  def characterName: String = _characterName

  override def opcode(): SendOpcode = SendOpcode.NOTIFY_JOB_CHANGE
}