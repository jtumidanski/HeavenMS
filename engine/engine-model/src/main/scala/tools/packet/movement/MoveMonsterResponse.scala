package tools.packet.movement

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MoveMonsterResponse(private var _objectId: Int, private var _moveId: Short, private var _currentMp: Int,
                          private var _useSkills: Boolean, private var _skillId: Int, private var _skillLevel: Int) extends PacketInput {
  def objectId: Int = _objectId

  def moveId: Short = _moveId

  def currentMp: Int = _currentMp

  def useSkills: Boolean = _useSkills

  def skillId: Int = _skillId

  def skillLevel: Int = _skillLevel

  def this(_objectId: Int, _moveId: Short, _currentMp: Int, _useSkills: Boolean) = {
    this(_objectId, _moveId, _currentMp, _useSkills, 0, 0)
  }

  override def opcode(): SendOpcode = SendOpcode.MOVE_MONSTER_RESPONSE
}