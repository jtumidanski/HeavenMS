package tools.packet.pet

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class PetNameChange(private var _characterId: Int, private var _newName: String, private var _slot: Int) extends PacketInput {
  def characterId: Int = _characterId

  def newName: String = _newName

  def slot: Int = _slot

  override def opcode(): SendOpcode = SendOpcode.PET_NAME_CHANGE
}