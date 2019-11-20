package tools.packet.monster

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CatchMonsterWithItem(private var _objectId: Int, private var _itemId: Int, private var _success: Byte) extends PacketInput {
  def objectId: Int = _objectId

  def itemId: Int = _itemId

  def success: Byte = _success

  override def opcode(): SendOpcode = SendOpcode.CATCH_MONSTER_WITH_ITEM
}