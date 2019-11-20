package tools.packet.guild.bbs

import client.database.data.BbsThreadData
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetThreadList(private var _threadData: java.util.List[BbsThreadData], private var _start: Int) extends PacketInput {
  def threadData: java.util.List[BbsThreadData] = _threadData

  def start: Int = _start

  override def opcode(): SendOpcode = SendOpcode.GUILD_BBS_PACKET
}