package tools.packet.rps

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RPSMode(private var _mode: Byte) extends PacketInput {
  def mode: Byte = _mode

  override def opcode(): SendOpcode = SendOpcode.RPS_GAME
}