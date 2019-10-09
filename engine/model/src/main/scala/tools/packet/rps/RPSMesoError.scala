package tools.packet.rps

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RPSMesoError(private var _mesos: Int) extends PacketInput {
  def mesos: Int = _mesos

  override def opcode(): SendOpcode = SendOpcode.RPS_GAME
}