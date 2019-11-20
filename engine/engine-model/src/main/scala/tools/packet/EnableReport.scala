package tools.packet

import net.opcodes.SendOpcode

class EnableReport() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.CLAIM_STATUS_CHANGED
}