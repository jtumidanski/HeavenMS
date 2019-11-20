package tools.packet.showitemgaininchat

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowOwnBuffEffect(private var _skillId: Int, private var _effectId: Int) extends PacketInput {
  def skillId: Int = _skillId

  def effectId: Int = _effectId

  override def opcode(): SendOpcode = SendOpcode.SHOW_ITEM_GAIN_INCHAT
}