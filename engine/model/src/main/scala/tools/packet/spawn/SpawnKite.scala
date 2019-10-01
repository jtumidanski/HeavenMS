package tools.packet.spawn

import java.awt.Point

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SpawnKite(private var _objectId: Int, private var _itemId: Int, private var _name: String,
                private var _message: String, private var _position: Point, private var _ft: Int) extends PacketInput {
  def objectId: Int = _objectId

  def itemId: Int = _itemId

  def name: String = _name

  def message: String = _message

  def position: Point = _position

  def ft: Int = _ft

  override def opcode(): SendOpcode = SendOpcode.SPAWN_KITE
}