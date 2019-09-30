package tools.packet.playerinteraction

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetMiniGameStart(private var _loser: Int) extends PacketInput {
  def loser: Int = _loser

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}