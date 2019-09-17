package net.server.channel.packet.movement

import java.awt.Point

import net.server.MovementData

class MoveDragonPacket(private var _startPosition: Point, private var _hasMovement: Boolean, private var _movementDataList: java.util.List[MovementData], private var _movementList: java.util.List[java.lang.Byte]) extends BaseMovementPacket {
  def startPosition: Point = _startPosition

  def hasMovement: Boolean = _hasMovement

  def movementDataList: java.util.List[MovementData] = _movementDataList

  def movementList: java.util.List[java.lang.Byte] = _movementList
}
