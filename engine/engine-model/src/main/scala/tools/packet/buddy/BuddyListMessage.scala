package tools.packet.buddy

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class BuddyListMessage(private var _message: Byte) extends PacketInput {
  def message: Byte = _message

  override def opcode(): SendOpcode = SendOpcode.BUDDY_LIST
}