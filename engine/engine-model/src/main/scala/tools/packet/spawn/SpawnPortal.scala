package tools.packet.spawn

import java.awt.Point

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SpawnPortal(private var _townId: Int, private var _targetId: Int, private var _position: Point) extends PacketInput {
  def townId: Int = _townId

  def targetId: Int = _targetId

  def position: Point = _position

  override def opcode(): SendOpcode = SendOpcode.SPAWN_PORTAL
}