package tools.packet.character.interaction

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetTradeMeso(private var _number: Byte, private var _meso: Int) extends PacketInput {
  def number: Byte = _number

  def meso: Int = _meso

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}