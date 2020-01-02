package tools.packet.buff

import client.MapleBuffStat
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CancelBuff(private var _statIncreases: java.util.List[MapleBuffStat]) extends PacketInput {
  def statIncreases: java.util.List[MapleBuffStat] = _statIncreases

  override def opcode(): SendOpcode = SendOpcode.CANCEL_BUFF
}