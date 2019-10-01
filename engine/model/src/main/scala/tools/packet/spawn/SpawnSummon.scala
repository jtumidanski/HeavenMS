package tools.packet.spawn

import java.awt.Point

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SpawnSummon(private var _ownerId: Int, private var _objectId: Int, private var _skillId: Int,
                  private var _skillLevel: Int, private var _position: Point, private var _stance: Int,
                  private var _movementType: Int, private var _puppet: Boolean, private var _animated: Boolean) extends PacketInput {
  def ownerId: Int = _ownerId

  def objectId: Int = _objectId

  def skillId: Int = _skillId

  def skillLevel: Int = _skillLevel

  def position: Point = _position

  def stance: Int = _stance

  def movementType: Int = _movementType

  def puppet: Boolean = _puppet

  def animated: Boolean = _animated

  override def opcode(): SendOpcode = SendOpcode.SPAWN_SPECIAL_MAPOBJECT
}