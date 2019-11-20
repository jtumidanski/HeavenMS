package net.server.channel.packet.movement

import net.server.MovementData

class MoveLifePacket(private var _objectId: Int, private var _moveId: Short, private var _pNibbles: Byte,
                     private var _rawActivity: Byte, private var _skillId: Int, private var _skillLevel: Int,
                     private var _pOption: Short, private var _startX: Short, private var _startY: Short,
                     private var _hasMovement: Boolean, private var _movementDataList: java.util.List[MovementData],
                     private var _movementList: java.util.List[java.lang.Byte]) extends BaseMovementPacket {
  def objectId: Int = _objectId

  def moveId: Short = _moveId

  def pNibbles: Byte = _pNibbles

  def rawActivity: Byte = _rawActivity

  def skillId: Int = _skillId

  def skillLevel: Int = _skillLevel

  def pOption: Short = _pOption

  def startX: Short = _startX

  def startY: Short = _startY

  def hasMovement: Boolean = _hasMovement

  def movementDataList: java.util.List[MovementData] = _movementDataList

  def movementList: java.util.List[java.lang.Byte] = _movementList
}
