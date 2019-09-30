package tools.packet.serverlist

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ServerListEnd() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.SERVERLIST
}