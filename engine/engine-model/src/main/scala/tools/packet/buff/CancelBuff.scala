package tools.packet.buff

import client.MapleBuffStat
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CancelBuff(private var _statups: java.util.List[MapleBuffStat]) extends PacketInput {
  def statups: java.util.List[MapleBuffStat] = _statups

  override def opcode(): SendOpcode = SendOpcode.CANCEL_BUFF
}