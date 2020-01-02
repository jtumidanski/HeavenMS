package tools.packet.serverlist

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetServerStatus(private var _status: ServerStatus) extends PacketInput {
  def status: ServerStatus = _status

  def this(serverStatusId: Int) = {
    this(ServerStatus.fromValue(serverStatusId))
  }

  override def opcode(): SendOpcode = SendOpcode.SERVER_STATUS
}
