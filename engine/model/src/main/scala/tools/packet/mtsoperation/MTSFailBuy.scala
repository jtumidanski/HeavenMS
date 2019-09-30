package tools.packet.mtsoperation

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MTSFailBuy() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.MTS_OPERATION
}