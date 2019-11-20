package tools.packet.item.enhance

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SendHammer(private var _used: Int) extends PacketInput {
  def used: Int = _used

  override def opcode(): SendOpcode = SendOpcode.VICIOUS_HAMMER
}