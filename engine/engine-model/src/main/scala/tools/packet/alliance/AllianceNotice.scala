package tools.packet.alliance

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class AllianceNotice(private var _id: Int, private var _notice: String) extends PacketInput {
  def id: Int = _id

  def notice: String = _notice

  override def opcode(): SendOpcode = SendOpcode.ALLIANCE_OPERATION
}