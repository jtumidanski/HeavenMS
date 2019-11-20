package tools.packet.pet

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class PetExceptionList(private var _characterId: Int, private var _petId: Int, private var _petIndex: Byte,
                       private var _exclusionList: java.util.List[java.lang.Integer]) extends PacketInput {
  def characterId: Int = _characterId

  def petId: Int = _petId

  def petIndex: Byte = _petIndex

  def exclusionList: java.util.List[java.lang.Integer] = _exclusionList

  override def opcode(): SendOpcode = SendOpcode.PET_EXCEPTION_LIST
}