package tools.packet.fredrick

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetFredrickInfo(private var _characterId: Int, private var _merchantNetMeso: Int) extends PacketInput {
  def characterId: Int = _characterId

  def merchantNetMeso: Int = _merchantNetMeso

  override def opcode(): SendOpcode = SendOpcode.FREDRICK
}