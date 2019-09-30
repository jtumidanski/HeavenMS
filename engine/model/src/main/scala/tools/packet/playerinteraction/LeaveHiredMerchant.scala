package tools.packet.playerinteraction

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class LeaveHiredMerchant(private var _slot: Int, private var _status: Int) extends PacketInput {
  def slot: Int = _slot

  def status: Int = _status

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}