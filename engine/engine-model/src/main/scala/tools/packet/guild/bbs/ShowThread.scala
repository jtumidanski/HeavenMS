package tools.packet.guild.bbs

import client.database.data.BbsThreadData
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowThread(private var _localThreadId: Int, private var _threadData: BbsThreadData) extends PacketInput {
  def localThreadId: Int = _localThreadId

  def threadData: BbsThreadData = _threadData

  override def opcode(): SendOpcode = SendOpcode.GUILD_BBS_PACKET
}