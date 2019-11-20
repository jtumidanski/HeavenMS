package tools.packet.statusinfo

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowMesoGain(private var _gain: Int, private var _inChat: Boolean) extends PacketInput {
  def gain: Int = _gain

  def inChat: Boolean = _inChat

  override def opcode(): SendOpcode = SendOpcode.SHOW_STATUS_INFO
}