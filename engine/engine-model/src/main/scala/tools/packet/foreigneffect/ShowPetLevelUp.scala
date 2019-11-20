package tools.packet.foreigneffect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowPetLevelUp(private var _characterId: Int, private var _index: Byte) extends PacketInput {
  def characterId: Int = _characterId

  def index: Byte = _index

  override def opcode(): SendOpcode = SendOpcode.SHOW_FOREIGN_EFFECT
}