package tools.packet.statusinfo

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowFameGain(private var _gain: Int) extends PacketInput {
  def gain: Int = _gain

  override def opcode(): SendOpcode = SendOpcode.SHOW_STATUS_INFO
}