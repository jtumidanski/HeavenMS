package tools.packet.movement

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MovePlayer(private var _characterId: Int, private var _movementList: java.util.List[java.lang.Byte]) extends PacketInput {
  def characterId: Int = _characterId

  def movementList: java.util.List[java.lang.Byte] = _movementList

  override def opcode(): SendOpcode = SendOpcode.MOVE_PLAYER
}