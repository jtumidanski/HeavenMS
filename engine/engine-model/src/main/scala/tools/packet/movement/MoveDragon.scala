package tools.packet.movement

import java.awt.Point

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MoveDragon(private var _ownerId: Int, private var _startPosition: Point, private var _movementList: java.util.List[java.lang.Byte]) extends PacketInput {
  def ownerId: Int = _ownerId

  def startPosition: Point = _startPosition

  def movementList: java.util.List[java.lang.Byte] = _movementList

  override def opcode(): SendOpcode = SendOpcode.MOVE_DRAGON
}