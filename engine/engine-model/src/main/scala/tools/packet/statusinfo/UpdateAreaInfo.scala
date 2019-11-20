package tools.packet.statusinfo

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class UpdateAreaInfo(private var _areaId: Int, private var _info: String) extends PacketInput {
  def areaId: Int = _areaId

  def info: String = _info

  override def opcode(): SendOpcode = SendOpcode.SHOW_STATUS_INFO
}