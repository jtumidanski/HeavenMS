package tools.packet.character

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class FacialExpression(private var _characterId: Int, private var _expression: Int) extends PacketInput {
  def characterId: Int = _characterId

  def expression: Int = _expression

  override def opcode(): SendOpcode = SendOpcode.FACIAL_EXPRESSION
}