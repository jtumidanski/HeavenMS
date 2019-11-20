package tools.packet.pet

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class PetFoodResponse(private var _characterId: Int, private var _index: Byte, private var _success: Boolean, private var _balloonType: Boolean) extends PacketInput {
  def characterId: Int = _characterId

  def index: Byte = _index

  def success: Boolean = _success

  def balloonType: Boolean = _balloonType

  override def opcode(): SendOpcode = SendOpcode.PET_COMMAND
}