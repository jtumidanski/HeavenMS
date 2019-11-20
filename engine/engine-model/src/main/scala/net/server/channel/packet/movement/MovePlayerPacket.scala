package net.server.channel.packet.movement

import net.server.{MaplePacket, MovementData}

class MovePlayerPacket( private var _hasMovement: Boolean,  private var _movementDataList: java.util.List[MovementData],  private var _movementList: java.util.List[java.lang.Byte]) extends BaseMovementPacket {
     def hasMovement: Boolean = _hasMovement
     def movementDataList: java.util.List[MovementData] = _movementDataList
     def movementList: java.util.List[java.lang.Byte] = _movementList
}
