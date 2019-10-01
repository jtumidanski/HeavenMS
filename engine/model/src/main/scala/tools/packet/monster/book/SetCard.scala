package tools.packet.monster.book

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SetCard(private var _full: Boolean, private var _cardId: Int, private var _level: Int) extends PacketInput {
  def full: Boolean = _full

  def cardId: Int = _cardId

  def level: Int = _level

  override def opcode(): SendOpcode = SendOpcode.MONSTER_BOOK_SET_CARD
}