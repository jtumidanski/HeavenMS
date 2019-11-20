package tools.packet.movement

import java.awt.Point

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MoveMonster(private var _objectId: Int, private var _skillPossible: Boolean, private var _skill: Int,
                  private var _skillId: Int, private var _skillLevel: Int, private var _option: Int,
                  private var _startPosition: Point, private var _movementList: java.util.List[java.lang.Byte]) extends PacketInput {
  def objectId: Int = _objectId

  def skillPossible: Boolean = _skillPossible

  def skill: Int = _skill

  def skillId: Int = _skillId

  def skillLevel: Int = _skillLevel

  def option: Int = _option

  def startPosition: Point = _startPosition

  def movementList: java.util.List[java.lang.Byte] = _movementList

  override def opcode(): SendOpcode = SendOpcode.MOVE_MONSTER
}