package tools.packet.playerinteraction

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetTradeResult(private var _number: Byte, private var _operation: Byte) extends PacketInput {
  def number: Byte = _number

  def operation: Byte = _operation

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}