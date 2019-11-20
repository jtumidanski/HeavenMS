package tools.packet.showitemgaininchat

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowSpecialEffect(private var _effectId: Int) extends PacketInput {
  def effectId: Int = _effectId

  override def opcode(): SendOpcode = SendOpcode.SHOW_ITEM_GAIN_INCHAT
}