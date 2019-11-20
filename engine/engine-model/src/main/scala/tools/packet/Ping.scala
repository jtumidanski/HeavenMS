package tools.packet

import net.opcodes.SendOpcode

class Ping() extends PacketInput {
  override def opcode(): SendOpcode = SendOpcode.PING
}