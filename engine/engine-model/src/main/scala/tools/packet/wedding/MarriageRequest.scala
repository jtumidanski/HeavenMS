package tools.packet.wedding

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MarriageRequest(private var _name: String, private var _characterId: Int) extends PacketInput {
  def name: String = _name

  def characterId: Int = _characterId

  override def opcode(): SendOpcode = SendOpcode.MARRIAGE_REQUEST
}