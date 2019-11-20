package tools.packet.guild

import client.database.data.GlobalUserRank
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowPlayerRanks(private var _npcId: Int, private var _ranks: java.util.List[GlobalUserRank]) extends PacketInput {
  def npcId: Int = _npcId

  def ranks: java.util.List[GlobalUserRank] = _ranks

  override def opcode(): SendOpcode = SendOpcode.GUILD_OPERATION
}