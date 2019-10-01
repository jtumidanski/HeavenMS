package tools.packet.monster.book

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ChangeCover(private var _cardId: Int) extends PacketInput {
  def cardId: Int = _cardId

  override def opcode(): SendOpcode = SendOpcode.MONSTER_BOOK_SET_COVER
}