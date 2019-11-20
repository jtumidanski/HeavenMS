package tools.packet.foreigneffect

import client.inventory.ScrollResult
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowScrollEffect(private var _characterId: Int, private var _success: ScrollResult, private var _legendarySpirit: Boolean, private var _whiteScroll: Boolean) extends PacketInput {
  def characterId: Int = _characterId

  def success: ScrollResult = _success

  def legendarySpirit: Boolean = _legendarySpirit

  def whiteScroll: Boolean = _whiteScroll

  override def opcode(): SendOpcode = SendOpcode.SHOW_SCROLL_EFFECT
}