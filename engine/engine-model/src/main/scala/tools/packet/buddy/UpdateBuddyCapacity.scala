package tools.packet.buddy

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class UpdateBuddyCapacity(private var _capacity: Int) extends PacketInput {
  def capacity: Int = _capacity

  override def opcode(): SendOpcode = SendOpcode.BUDDYLIST
}