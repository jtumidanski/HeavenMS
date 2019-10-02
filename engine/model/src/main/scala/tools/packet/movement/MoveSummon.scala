package tools.packet.movement

import java.awt.Point

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MoveSummon(private var _characterId: Int, private var _objectId: Int, private var _startPosition: Point, private var _movementList: java.util.List[java.lang.Byte]) extends PacketInput {
  def characterId: Int = _characterId

  def objectId: Int = _objectId

  def startPosition: Point = _startPosition

  def movementList: java.util.List[java.lang.Byte] = _movementList

  override def opcode(): SendOpcode = SendOpcode.MOVE_SUMMON
}