package tools.packet.spawn

import java.awt.Point

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SpawnDoor(private var _ownerId: Int, private var _position: Point, private var _launched: Boolean) extends PacketInput {
  def ownerId: Int = _ownerId

  def position: Point = _position

  def launched: Boolean = _launched

  override def opcode(): SendOpcode = SendOpcode.SPAWN_DOOR
}