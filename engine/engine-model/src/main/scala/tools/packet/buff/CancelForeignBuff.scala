package tools.packet.buff

import client.MapleBuffStat
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CancelForeignBuff(private var _characterId: Int, private var _statups: java.util.List[MapleBuffStat]) extends PacketInput {
  def characterId: Int = _characterId

  def statups: java.util.List[MapleBuffStat] = _statups

  override def opcode(): SendOpcode = SendOpcode.CANCEL_FOREIGN_BUFF
}