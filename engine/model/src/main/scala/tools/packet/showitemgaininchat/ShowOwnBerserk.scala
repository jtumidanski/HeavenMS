package tools.packet.showitemgaininchat

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowOwnBerserk(private var _skillLevel: Int, private var _berserk: Boolean) extends PacketInput {
  def skillLevel: Int = _skillLevel

  def berserk: Boolean = _berserk

  override def opcode(): SendOpcode = SendOpcode.SHOW_ITEM_GAIN_INCHAT
}