package tools.packet.movement

import net.opcodes.SendOpcode
import server.movement.LifeMovementFragment
import tools.packet.PacketInput

class MovePet(private var _characterId: Int, private var _petId: Int, private var _slot: Byte,
              private var _movementList: java.util.List[LifeMovementFragment]) extends PacketInput {
  def characterId: Int = _characterId

  def petId: Int = _petId

  def slot: Byte = _slot

  def movementList: java.util.List[LifeMovementFragment] = _movementList

  override def opcode(): SendOpcode = SendOpcode.MOVE_PET
}