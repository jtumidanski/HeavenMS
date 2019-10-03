package tools.packet.pet

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class PetCommandResponse(private var _characterId: Int, private var _index: Byte, private var _talk: Boolean,
                         private var _animation: Int, private var _balloonType: Boolean) extends PacketInput {
  def characterId: Int = _characterId

  def index: Byte = _index

  def talk: Boolean = _talk

  def animation: Int = _animation

  def balloonType: Boolean = _balloonType

  override def opcode(): SendOpcode = SendOpcode.PET_COMMAND
}