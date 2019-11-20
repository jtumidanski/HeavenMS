package tools.packet.statusinfo

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowInventoryFull() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.SHOW_STATUS_INFO
}