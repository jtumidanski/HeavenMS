package tools.packet.shop

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class UpdateHiredMerchantBox(private var _ownerId: Int, private var _objectId: Int, private var _description: String,
                             private var _itemId: Int, private var _roomInto: Array[Byte]) extends PacketInput {
  def ownerId: Int = _ownerId

  def objectId: Int = _objectId

  def description: String = _description

  def itemId: Int = _itemId

  def roomInto: Array[Byte] = _roomInto

  override def opcode(): SendOpcode = SendOpcode.UPDATE_HIRED_MERCHANT
}