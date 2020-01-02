package tools.packet.buff

import client.MapleBuffStat
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CancelForeignBuff(private var _characterId: Int, private var _statIncreases: java.util.List[MapleBuffStat]) extends PacketInput {
  def characterId: Int = _characterId

  def statIncreases: java.util.List[MapleBuffStat] = _statIncreases

  override def opcode(): SendOpcode = SendOpcode.CANCEL_FOREIGN_BUFF
}