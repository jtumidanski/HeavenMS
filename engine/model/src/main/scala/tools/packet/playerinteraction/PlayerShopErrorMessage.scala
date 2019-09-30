package tools.packet.playerinteraction

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class PlayerShopErrorMessage(private var _error: Int, private var _type: Int) extends PacketInput {
  def error: Int = _error

  def theType: Int = _type

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}