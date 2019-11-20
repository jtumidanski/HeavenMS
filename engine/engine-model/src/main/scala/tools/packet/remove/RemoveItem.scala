package tools.packet.remove

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RemoveItem(private var _objectId: Int, private var _animation: Int, private var _characterId: Int,
                 private var _pet: Boolean, private var _slot: Int) extends PacketInput {
  def objectId: Int = _objectId

  def animation: Int = _animation

  def characterId: Int = _characterId

  def pet: Boolean = _pet

  def slot: Int = _slot

  def this(_objectId: Int, _animation:Int, _characterId:Int) = {
    this(_objectId, _animation, _characterId, false, 0)
  }

  def this(_objectId: Int) = this(_objectId, 1, 0)

  override def opcode() = SendOpcode.REMOVE_ITEM_FROM_MAP
}